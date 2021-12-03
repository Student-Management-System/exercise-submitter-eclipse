package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.replay.ReplayException;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer.Version;

/**
 * A background job that executes check submission.
 * @author lukas
 *
 */
public class CheckSubmissionJob extends Job {

    private static ILock lock = Job.getJobManager().newLock();

    private Shell shell;
    private Replayer replayer;
    private Assignment assignment;
    private Consumer<CheckSubmissionJob> callbackCheckSubmission;
    private File dir;
    private List<Version> versionlist;
    private Optional<Version> version = Optional.empty();
    private Optional<Boolean> result = Optional.empty();
    
    
    /**
     * Creats an instance of CheckSubmissionJob.
     * @param shell , the current window shell
     * @param replayer , authenticated replayer
     * @param assignment , selected assigment
     * @param toCheck , the dir to check
     * @param callbackCheckSubmission
     */
    public CheckSubmissionJob(Shell shell, Replayer replayer, Assignment assignment, File toCheck,
            Consumer<CheckSubmissionJob> callbackCheckSubmission) {
        super("Check SubmissionJob");
        this.shell = shell;
        this.replayer = replayer;
        this.assignment = assignment;
        this.dir = toCheck;
        this.callbackCheckSubmission = callbackCheckSubmission;
    }
    /**
     * Class for managing the checkresult.
     * @author lukas
     *
     */
    public static class CheckResult {
        private boolean result;
        private Assignment assignment;
        /**
         * Instantiates a new CheckResult class.
         * @param result , is the content the same
         * @param assignment , the compared assignment
         */
        public CheckResult(boolean result, Assignment assignment) {
            this.result = result;
            this.assignment = assignment;
        }
        /**
         * Gets the result.
         * @return boolean , is the same ?
         */
        public boolean getResult() {
            return result;
        }
        /**
         * Gets the compared assignment.
         * @return assignment
         */
        public Assignment getAssignment() {
            return assignment;
        }
    }

    @Override
    protected IStatus run(IProgressMonitor arg0) {

        try {
            lock.acquire();

            versionlist = this.replayer.getVersions();

            if (versionlist.size() == 0) {
                throw new ReplayException("No version is uploaded");
            }

            this.version = Optional.ofNullable(versionlist.get(0));

            this.result = Optional.ofNullable(this.replayer.isSameContent(this.dir, this.version.get()));

            Display.getDefault().asyncExec(() -> {
                this.callbackCheckSubmission.accept(this);
            });

        } catch (ReplayException | IOException e) {
            Display.getDefault().asyncExec(() -> {
                if (e instanceof ReplayException && e.getMessage().equals("No version is uploaded")) {
                    MessageDialog.openError(this.shell, "Check Submission", e.getMessage());
                } else {
                    ExceptionDialogs.showUnexpectedExceptionDialog(e, "Failed check submission");
                }
            });
        } finally {
            lock.release();
        }
        return Status.OK_STATUS;
    }
    /**
     * Get the Version the dir gets compared.
     * @return Optional of Version
     */
    public Optional<Version> getVersion() {
        return this.version;
    }
    /**
     * Get the result of the comparison.
     * @return Optional of Boolean
     */
    public CheckResult getCheckResult() {
        return new CheckResult(this.result.get(), this.assignment);
    }
    /**
     * Get the current shell.
     * @return shell
     */
    public Shell getShell() {
        return shell;
    }
    /**
     * Gets the current replayer.
     * @return Replayer
     */
    public Replayer getReplayer() {
        return replayer;
    }
    /**
     * Gets the current versionlist.
     * @return List<Version>
     */
    public List<Version> getVersionlist() {
        return versionlist;
    }
    
   

}
