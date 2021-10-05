package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
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

            Display.getDefault().asyncExec(() -> {
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
     */
    private void startReplay() throws ReplayException, IOException {
        IProgressMonitor monitor = this.progress;
        monitor.worked(50);
        monitor.setTaskName("Downloading Files");

        SubMonitor subMonitorReplay = SubMonitor.convert(monitor, 65);
        subMonitorReplay.beginTask("Downloading Files", 100);

        if (this.location.isEmpty()) {
            throw new ReplayException("No file location");
        }

        File tempdir = this.replayer.replay(this.version.get());

        this.copyProject(tempdir.toPath(), this.location.get().toPath());

        monitor.worked(50);

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

    }

    /**
     * Get called when versionlist is ready.
     *
     * @param job
     */
    private void onListVersionsFinished(ListVersionsJob job) {
        try {
            this.version = job.getSelectedVersion();
            this.createIProject();
            this.startReplay();
        } catch (ReplayException | IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Creates a IProject.
     *
     */
    private void createIProject() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject newProject = root.getProject(this.getAssignment().getName() + "-"
                + this.getVersion().get().getTimestamp().format(DateTimeFormatter.BASIC_ISO_DATE));
        try {
            newProject.create(null);
            newProject.open(null);
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.location = Optional.ofNullable(newProject.getLocation().toFile());
    }

    /**
     * Copies the project from the temp folder to the project folder.
     *
     * @param firstdir
     * @param seconddir
     * @throws IOException
     */
    private void copyProject(Path firstdir, Path seconddir) throws IOException {
        Files.walk(firstdir).forEach(sourceFile -> {
            Path destination = seconddir.resolve(firstdir.relativize(sourceFile));
            if (!destination.equals(seconddir)) {
                try {
                    Files.copy(sourceFile, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

        });
    }

}
