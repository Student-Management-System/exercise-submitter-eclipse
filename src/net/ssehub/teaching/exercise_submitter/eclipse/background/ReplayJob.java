package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.VersionSelectionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.labels.ProjectAssignmentMapper;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.eclipse.utils.TimeUtils;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.replay.ReplayException;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer.Version;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.GroupNotFoundException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;

/**
 * A job that replays a submission to a given assignment into a new local project.
 *   
 * @author Adam
 * @author lukas
 */
public class ReplayJob extends AbstractJob<IProject> {

    private ExerciseSubmitterManager manager;
    
    private Assignment assignment;
    
    /**
     * Creates a new replay job.
     * 
     * @param shell The parent shell to show dialogs for.
     * @param manager The manager to contact the student management system.
     * @param assignment The assignment to replay.
     * @param callback The callback called when the project with the submission has been created.
     */
    public ReplayJob(Shell shell, ExerciseSubmitterManager manager, Assignment assignment,
            Consumer<IProject> callback) {
        super("Replay Submission", shell, callback);
        this.manager = manager;
        this.assignment = assignment;
    }

    @Override
    protected Optional<IProject> run() {
        Optional<IProject> result = Optional.empty();
        
        this.monitor.beginTask("Replaying Submission", 4);
        
        try (Replayer replayer = this.manager.getReplayer(this.assignment)) {
            this.monitor.subTask("Getting versions");
            VersionSelectionDialog versionDialog = new VersionSelectionDialog(this.shell, this.assignment.getName(),
                    replayer.getVersions());
            this.monitor.worked(1);
            
            this.monitor.subTask("Selecting Version");
            AtomicReference<Optional<Version>> dialogResult = new AtomicReference<>(Optional.empty());
            this.shell.getDisplay().syncExec(() -> {
                dialogResult.set(versionDialog.openAndGetSelectedVersion());
            });
            this.monitor.worked(1);
            
            if (dialogResult.get().isPresent()) {
                Version version = dialogResult.get().get();

                this.monitor.subTask("Downloading Submission");
                File replay = replayer.replay(version);
                this.monitor.worked(1);
                
                this.monitor.subTask("Creating Project");
                Optional<IProject> projectCreation = createProject(version);
                if (projectCreation.isPresent()) {
                    IProject project = projectCreation.get();
                    copyProjectContent(replay.toPath(), project.getLocation().toFile().toPath());
                    project.refreshLocal(IResource.DEPTH_INFINITE, null);
                    markProjectContentReadOnly(project);
                    
                    ProjectAssignmentMapper.INSTANCE.setAssociation(project, this.assignment);
                    
                    result = Optional.of(project);
                }
                this.monitor.worked(1);
            }
            
        } catch (NetworkException e) {
            ExceptionDialogs.showNetworkExceptionDialog(e);
        } catch (UserNotInCourseException e) {
            ExceptionDialogs.showUserNotInCourseDialog(this.manager.getCourse().getId());
        } catch (AuthenticationException e) {
            ExceptionDialogs.showLoginFailureDialog();
        } catch (GroupNotFoundException e) {
            ExceptionDialogs.showUserNotInGroupDialog(this.assignment.getName());
        } catch (ApiException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Generic API exception");
        } catch (IOException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Failed to store replayed version");
        } catch (ReplayException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Failed to replay");
        } catch (CoreException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Failed to create the project");
        }
        
        return result;
    }

    /**
     * Creates a project with the name of this assignment and the given version.
     * 
     * @param version The version to create the project for. Will be used in the project name.
     * 
     * @return The created project.
     * 
     * @throws CoreException If creating the project failed.
     * @throws IOException If writing the default <code>.classpath</code> file fails.
     */
    private Optional<IProject> createProject(Version version) throws CoreException, IOException {
        Instant timestamp = version.getTimestamp();
        String projectName = this.assignment.getName() + "-" + TimeUtils.instantToLocalStringNoColons(timestamp);
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        
        IProject newProject = root.getProject(projectName);
        
        if (newProject.exists()) {
            AtomicBoolean delete = new AtomicBoolean(false);
            this.shell.getDisplay().syncExec(() -> {
                boolean selected = MessageDialog.openQuestion(this.shell, "Project Exists",
                        "A project with the name " + projectName + " already exists. Do you want to override it?\n\n"
                                + "Warning: this deletes all previous content of the project.");
                delete.set(selected);
            });
            if (delete.get()) {
                newProject.delete(true, true, null);
            } else {
                newProject = null;
            }
        }
        
        if (newProject != null) {
            newProject.create(null);
            newProject.open(null);
            
            try {
                IProjectDescription description = newProject.getDescription();
                description.setNatureIds(new String[] {"org.eclipse.jdt.core.javanature"});
                newProject.setDescription(description, null);
            } catch (CoreException e) {
                EclipseLog.warning("Failed to set java nature for new project: " + e.getMessage());
            }
            
            InputStream in = getClass().getResourceAsStream(".classpath");
            if (in != null) {
                Files.copy(in, newProject.getLocation().toFile().toPath().resolve(".classpath"));
            } else {
                throw new IOException(".classpath resource not found");
            }
        }
        
        return Optional.ofNullable(newProject);
    }
    
    /**
     * Copies the content from the given replay folder into the given project folder.
     * 
     * @param replayDirectory The location where the replay is stored.
     * @param projectDirectory The location of the project.
     * 
     * @throws IOException If copying files fails.
     */
    private void copyProjectContent(Path replayDirectory, Path projectDirectory) throws IOException {
        try {
            Files.walk(replayDirectory).forEach(sourceFile -> {
                Path targetFile = projectDirectory.resolve(replayDirectory.relativize(sourceFile));
                
                try {
                    if (Files.isDirectory(sourceFile)) {
                        if (!Files.exists(targetFile)) {
                            Files.createDirectory(targetFile);
                        }
                    } else {
                        Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }
    
    /**
     * Marks all contents in the given project (excluding <code>.project</code>) as read-only.
     * @param project
     */
    private void markProjectContentReadOnly(IProject project) {
        try {
            project.accept((IResourceVisitor) resource -> {
                if (!resource.equals(project) && !resource.getName().equals(".project")) {
                    ResourceAttributes attributes = resource.getResourceAttributes();
                    attributes.setReadOnly(true);
                    resource.setResourceAttributes(attributes);
                }
                return true;
            });
        } catch (CoreException e) {
            EclipseLog.warning("Failed to mark project as read-only: " + e.getMessage());
        }
    }

}
