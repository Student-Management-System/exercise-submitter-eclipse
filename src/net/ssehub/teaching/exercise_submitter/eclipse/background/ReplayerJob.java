package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.VersionDialog;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.replay.ReplayException;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer.Version;
/**
 *  A background job that executes the replay.
 * @author lukas
 *
 */
public class ReplayerJob extends Job {

    private static ILock lock = Job.getJobManager().newLock();
    private Consumer<ReplayerJob> callbackVersionlist;
    private Consumer<ReplayerJob> callbackReplay;
    private Shell shell;
    private Optional<Version> version = Optional.empty();
    private Replayer replayer;
    private Optional<File> location = Optional.empty();
    private Assignment assignment;

    /**
     * Creates an instance.
     * 
     * @param shell , current window shell
     * @param replayer , authenticated replayer
     * @param assignment
     * @param callbackVersionlist
     * @param callbackReplay
     */
    public ReplayerJob(Shell shell, Replayer replayer, Assignment assignment, Consumer<ReplayerJob> callbackVersionlist,
            Consumer<ReplayerJob> callbackReplay) {
        super("Replayer Job");
        this.shell = shell;
        this.replayer = replayer;
        this.assignment = assignment;
        this.callbackVersionlist = callbackVersionlist;
        this.callbackReplay = callbackReplay;

    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Downloading Versionlist", 100);

        try {
            lock.acquire();

            List<Version> version = this.replayer.getVersions();

            monitor.worked(50);
            monitor.setTaskName("Downloading Files");

            Display.getDefault().syncExec(() -> {
                this.createVersionDialog(version);
            });

            SubMonitor subMonitorReplay = SubMonitor.convert(monitor, 65);
            subMonitorReplay.beginTask("Downloading Files", 100);

            if (this.version.isPresent()) {
                this.callbackVersionlist.accept(this);
            } else {
                throw new ReplayException("No version selected");
            }
            if (this.location.isEmpty()) {
                throw new ReplayException("No file location");
            }

            File tempdir = this.replayer.replay(this.version.get());
            
            this.copyProject(tempdir.toPath(), this.location.get().toPath());
            

            monitor.worked(50);

            Display.getDefault().asyncExec(() -> {
                this.callbackReplay.accept(this);
            });
        } catch (IllegalArgumentException | IOException | ReplayException ex) {
            Display.getDefault().asyncExec(() -> {
                AdvancedExceptionDialog.showUnexpectedExceptionDialog(ex, "Failed to download");
            });

        } finally {
            lock.release();

        }

        return Status.OK_STATUS;
    }
    /**
     * Returns the Version that is selected.
     * @return Optional Version
     */
    public Optional<Version> getVersion() {
        return this.version;
    }
    /**
     * Sets the File location for the replayer.
     * @param location
     */
    public void setLocation(Optional<File> location) {
        this.location = location;
    }
    /**
     * Gets the selected assignment.
     * @return Assignment
     */
    public Assignment getAssignment() {
        return this.assignment;
    }
    /**
     * Creates the versiondialog.
     * @param versions
     */
    private void createVersionDialog(List<Version> versions) {
        try {
            VersionDialog versionDialog = new VersionDialog(this.shell, versions);

            int dialogResult;

            do {

                dialogResult = versionDialog.open();
                // only use selected Assignment if user press ok
                if (dialogResult == Window.OK) {
                    this.version = versionDialog.getSelectedAssignment();
                }

                // user press ok without selecting anything. -> Retry
            } while (dialogResult == Window.OK && this.version.isEmpty());

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    /**
     * Copies the project from the temp folder to the project folder.
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
