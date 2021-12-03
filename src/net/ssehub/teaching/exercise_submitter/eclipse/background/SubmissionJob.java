package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.util.function.Consumer;

import org.eclipse.core.resources.IProject;
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
import net.ssehub.teaching.exercise_submitter.lib.submission.SubmissionException;
import net.ssehub.teaching.exercise_submitter.lib.submission.SubmissionResult;
import net.ssehub.teaching.exercise_submitter.lib.submission.Submitter;

/**
 * A background job that executes the submission.
 * 
 * @author Lukas
 * @author Adam
 */
public class SubmissionJob extends Job {

    private static ILock lock = Job.getJobManager().newLock();
    
    private Consumer<SubmissionJob> callback;
    
    private SubmissionResult result;
    private Submitter submitter;
    private IProject project;
    private Assignment assigment;
    private Shell shell;

    /**
     * Creates an instance.
     * 
     * @param submitter The submitter to use.
     * @param project The project to submit.
     * @param assignment The assignment to submit to.
     * @param shell The shell that the event originated from.
     * @param callback The callback to call once we are finished.
     */
    public SubmissionJob(Submitter submitter, IProject project, Assignment assignment, Shell shell,
            Consumer<SubmissionJob> callback) {
        super("Submission Job");
        this.callback = callback;
        
        this.submitter = submitter;
        this.result = null;
        this.project = project;
        this.assigment = assignment;
        this.shell = shell;
        
    }
    
    /**
     * Returns the project that is submitted.
     * 
     * @return The project.
     */
    public IProject getProject() {
        return project;
    }
    
    /**
     * Returns the assignment that is submitted to.
     *  
     * @return The assignment.
     */
    public Assignment getAssigment() {
        return assigment;
    }
    
    /**
     * Returns the {@link SubmissionResult} of the submission.
     * 
     * @return The {@link SubmissionResult} (<code>null</code> until submission is finished).
     */
    public SubmissionResult getSubmissionResult() {
        return result;
    }
    
    /**
     * Returns the shell that the starting event originated from.
     * 
     * @return The shell.
     */
    public Shell getShell() {
        return shell;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        monitor.beginTask("Transferring Files", BUILD);

        try {
            lock.acquire();
            this.result = this.submitter.submit(this.project.getLocation().toFile());
            Display.getDefault().asyncExec(() -> {
                this.callback.accept(this);
            });
        } catch (SubmissionException | IllegalArgumentException ex) {
            if (ex instanceof SubmissionException && ex.getLocalizedMessage().equals("Version is already submitted")) {
                Display.getDefault().asyncExec(() -> {
                    MessageDialog.openError(shell, "Submitter", "This project and the current"
                            + " project version on the Server are the same");
                });
            } else {
                Display.getDefault().asyncExec(() -> {
                    ExceptionDialogs.showUnexpectedExceptionDialog(ex, "Failed to submit");
                });
            }

        } finally {
            lock.release();

        }
        return Status.OK_STATUS;
    }

}
