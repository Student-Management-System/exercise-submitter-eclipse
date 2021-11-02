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
import java.util.function.Consumer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.eclipse.utils.FileUtils;
import net.ssehub.teaching.exercise_submitter.eclipse.utils.TimeUtils;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.replay.ReplayException;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer.Version;

/**
 * A background job that executes the replay.
 *
 * @author lukas
 *
 */
public class ReplayerJob extends Job {

    private static ILock lock = Job.getJobManager().newLock();
    private Consumer<ReplayerJob> callbackReplay;
    private Shell shell;
    private Optional<Version> version = Optional.empty();
    private Replayer replayer;
    private Optional<File> location = Optional.empty();
    private Assignment assignment;
    private IProgressMonitor progress;
    private Optional<IProject> project;

    /**
     * Creates an instance.
     *
     * @param shell          , current window shell
     * @param replayer       , authenticated replayer
     * @param assignment
     * @param callbackReplay
     */
    public ReplayerJob(Shell shell, Replayer replayer, Assignment assignment, Consumer<ReplayerJob> callbackReplay) {
        super("Replayer Job");
        this.shell = shell;
        this.replayer = replayer;
        this.assignment = assignment;
        this.callbackReplay = callbackReplay;

    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Downloading Versionlist", 100);

        try {
            lock.acquire();
            this.progress = monitor;

            this.createVersionDialog();

            Display.getDefault().syncExec(() -> {
                this.callbackReplay.accept(this);
            });
        } catch (IllegalArgumentException ex) {
            Display.getDefault().asyncExec(() -> {
                AdvancedExceptionDialog.showUnexpectedExceptionDialog(ex, "Failed to download");
            });

        } finally {
            lock.release();

        }

        return Status.OK_STATUS;
    }

    /**
     * Start replaying the selected version.
     *
     * @throws ReplayException
     * @throws IOException
     * @throws CoreException
     */
    private void startReplay() throws ReplayException, IOException, CoreException {
        IProgressMonitor monitor = this.progress;
        monitor.worked(50);
        monitor.setTaskName("Downloading Files");

        SubMonitor subMonitorReplay = SubMonitor.convert(monitor, 65);
        subMonitorReplay.beginTask("Downloading Files", 100);

        if (this.location.isEmpty()) {
            throw new ReplayException("No file location");
        }

        File tempdir = this.replayer.replay(this.version.get());

        copyProject(tempdir.toPath(), this.location.get().toPath());
        copyDefaultClasspath(this.location.get().toPath());

        monitor.worked(50);

        this.project.get().refreshLocal(IResource.DEPTH_INFINITE, subMonitorReplay);

        Display.getDefault().asyncExec(() -> {
            this.callbackReplay.accept(this);
        });

    }

    /**
     * Returns the Version that is selected.
     *
     * @return Optional Version
     */
    public Optional<Version> getVersion() {
        return this.version;
    }

    /**
     * Sets the File location for the replayer.
     *
     * @param location
     */
    public void setLocation(Optional<File> location) {
        this.location = location;
    }

    /**
     * Gets the selected assignment.
     *
     * @return Assignment
     */
    public Assignment getAssignment() {
        return this.assignment;
    }

    /**
     * Creates the versiondialog.
     *
     */
    private void createVersionDialog() {
        ListVersionsJob job = new ListVersionsJob(this.shell, this.replayer, this.assignment,
                this::onListVersionsFinished);
        job.setUser(true);
        job.schedule();
        //TODO: change method
        
       
        while (job.getState() == Job.RUNNING) {
            try {
                job.join();
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    /**
     * Get called when versionlist is ready.
     *
     * @param job
     */
    private void onListVersionsFinished(ListVersionsJob job) {
        try {
            this.version = job.getSelectedVersion();
            if (this.version.isPresent()) {
                if (this.createIProject()) {
                    this.startReplay();
                }
            }
        } catch (ReplayException | IOException | CoreException e) {

            e.printStackTrace();
        }
    }

    /**
     * Creates a IProject.
     *
     * @return boolean
     */
    private boolean createIProject() {
        boolean isCreated = false;
        
        Instant timestamp = version.get().getTimestamp();
        String projectName = this.getAssignment().getName() + "-" + TimeUtils.instantToLocalStringNoColons(timestamp);
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject newProject = root.getProject(projectName);
        try {
            newProject.create(null);
            newProject.open(null);
            this.project = Optional.ofNullable(newProject);
            isCreated = true;
        } catch (CoreException e) {

            AtomicBoolean dialogResult = new AtomicBoolean();
            if (e.getMessage().equals("Resource '/" + projectName + "' already exists.")) {

                Display.getDefault().syncExec(() -> {
                    dialogResult.set(MessageDialog.openQuestion(this.shell, "Download Submission",
                            "Project already exists " + projectName + "\n Should the content be overwritten ?"));

                });

                if (dialogResult.get()) {
                    FileUtils.deleteContentInFolder(newProject.getLocation().toFile());
                    isCreated = true;
                    System.out.println("ja");

                }
            }
        }
        this.location = Optional.ofNullable(newProject.getLocation().toFile());
        
        try {
            IProjectDescription description = newProject.getDescription();
            description.setNatureIds(new String[] {"org.eclipse.jdt.core.javanature"});
            newProject.setDescription(description, null);
        } catch (CoreException e) {
            EclipseLog.warning("Failed to set java nature for new project: " + e.getMessage());
        }
        
        return isCreated;
    }

    /**
     * Copies the default <code>.classpath</code> file from the resources to the given target directory.
     * 
     * @param targetDirectory The directory where to create the <code>.classpath</code> file in.
     * 
     * @throws IOException If creating the file fails.
     */
    private void copyDefaultClasspath(Path targetDirectory) throws IOException {
        InputStream in = getClass().getResourceAsStream(".classpath");
        if (in != null) {
            Files.copy(in, targetDirectory.resolve(".classpath"));
        } else {
            throw new IOException(".classpath resource not found");
        }
    }
    
    /**
     * Copies the project from the temp folder to the project folder.
     *
     * @param source
     * @param target
     * @throws IOException
     */
    private void copyProject(Path source, Path target) throws IOException {
        try {
            Files.walk(source).forEach(sourceFile -> {
                Path targetFile = target.resolve(source.relativize(sourceFile));
                
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

}
