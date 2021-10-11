package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
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
 * this class handles the Versiondialog as a background job.
 *
 * @author lukas
 *
 */
public class ListVersionsJob extends Job {

    private static ILock lock = Job.getJobManager().newLock();
    private Assignment assignment;
    private Optional<List<Version>> versionlist = Optional.empty();
    private Shell shell;
    private Replayer replayer;
    private Consumer<ListVersionsJob> callbackVersionlist;
    private Optional<Version> selectedVersion = Optional.empty();

    /**
     * Creates an instance of ListVersionsJob.
     *
     * @param shell
     * @param replayer
     * @param assignment
     * @param callbackVersionlist
     */
    public ListVersionsJob(Shell shell, Replayer replayer, Assignment assignment,
            Consumer<ListVersionsJob> callbackVersionlist) {
        super("List Versions");
        this.assignment = assignment;
        this.shell = shell;
        this.replayer = replayer;
        this.callbackVersionlist = callbackVersionlist;

    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Downloading Versionlist", 100);

        try {
            lock.acquire();

            this.versionlist = Optional.ofNullable(this.replayer.getVersions());

            Display.getDefault().syncExec(() -> {

                if (this.versionlist.isPresent()) {
                    if (this.versionlist.get().size() == 0) {
                        MessageDialog.openInformation(this.shell, "Exercise Submitter", "No Version available");
                    } else {
                        this.createVersionDialog(this.versionlist.get());
                    }
                }
            });

            Display.getDefault().syncExec(() -> {
                this.callbackVersionlist.accept(this);
            });
        } catch (IllegalArgumentException | ReplayException ex) {
            Display.getDefault().asyncExec(() -> {
                AdvancedExceptionDialog.showUnexpectedExceptionDialog(ex, "Failed to download versionlist");
            });

        } finally {
            lock.release();

        }

        return Status.OK_STATUS;
    }

    /**
     * Creates the versiondialog.
     *
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
                    this.selectedVersion = versionDialog.getSelectedAssignment();
                }

                // user press ok without selecting anything. -> Retry
            } while (dialogResult == Window.OK && this.selectedVersion.isEmpty());

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Gets the versionlist.
     *
     * @return Optional<List<Version>>
     */
    public Optional<List<Version>> getVersionlist() {
        return this.versionlist;
    }

    /**
     * Gets the selected version.
     *
     * @return Optional<Version> , the selected version
     */
    public Optional<Version> getSelectedVersion() {
        return this.selectedVersion;
    }

}
