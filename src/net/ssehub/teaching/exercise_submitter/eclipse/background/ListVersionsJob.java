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
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.VersionListDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.VersionSelectionDialog;
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
    private Optional<List<Version>> versionlist = Optional.empty();
    private Shell shell;
    private Replayer replayer;
    private Optional<Consumer<ListVersionsJob>> callbackVersionlist = Optional.empty();
    private Optional<Version> selectedVersion = Optional.empty();

    /**
     * Creates an instance of ListVersionsJob that calls the given callback after the user selected a version.
     *
     * @param shell
     * @param replayer
     * @param assignment
     * @param callbackVersionlist
     */
    public ListVersionsJob(Shell shell, Replayer replayer, Assignment assignment,
            Consumer<ListVersionsJob> callbackVersionlist) {
        super("List Versions");
        this.shell = shell;
        this.replayer = replayer;
        this.callbackVersionlist = Optional.of(callbackVersionlist);
    }
    
    /**
     * Creates an instance of ListVersionsJob that does not allow selection of a version.
     *
     * @param shell
     * @param replayer
     * @param assignment
     */
    public ListVersionsJob(Shell shell, Replayer replayer, Assignment assignment) {
        super("List Versions");
        this.shell = shell;
        this.replayer = replayer;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Downloading Versionlist", 100);

        try {
            lock.acquire();

            this.versionlist = Optional.ofNullable(this.replayer.getVersions());

            this.shell.getDisplay().syncExec(() -> {

                if (this.versionlist.isPresent()) {
                    if (this.versionlist.get().size() == 0) {
                        MessageDialog.openInformation(this.shell, "Exercise Submitter", "No Version available");
                    } else {
                        createVersionDialog(this.versionlist.get());
                    }
                }
            });

            if (callbackVersionlist.isPresent()) {
                this.shell.getDisplay().syncExec(() -> {
                    this.callbackVersionlist.get().accept(this);
                });
            }
        } catch (IllegalArgumentException | ReplayException ex) {
            this.shell.getDisplay().asyncExec(() -> {
                ExceptionDialogs.showUnexpectedExceptionDialog(ex, "Failed to download versionlist");
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
        if (callbackVersionlist.isPresent()) {
            VersionSelectionDialog versionDialog = new VersionSelectionDialog(this.shell, versions);
            
            int dialogResult;
            do {
                
                dialogResult = versionDialog.open();
                // only use selected Assignment if user press ok
                if (dialogResult == Window.OK) {
                    this.selectedVersion = versionDialog.getSelectedAssignment();
                }
                
                // user press ok without selecting anything. -> Retry
            } while (dialogResult == Window.OK && this.selectedVersion.isEmpty());
            
        } else {
            VersionListDialog versionDialog = new VersionListDialog(this.shell, versions);
            versionDialog.open();
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
