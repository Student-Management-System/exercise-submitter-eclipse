package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.util.function.Consumer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.lib.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.SubmissionException;
import net.ssehub.teaching.exercise_submitter.lib.SubmissionResult;
import net.ssehub.teaching.exercise_submitter.lib.Submitter;

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

    /**
     * Creates an instance.
     * 
     * @param submitter The submitter to use.
     * @param project The project to submit.
     * @param assignment The assignment to submit to.
     * @param callback The callback to call once we are finished.
     */
    public SubmissionJob(Submitter submitter, IProject project, Assignment assignment,
            Consumer<SubmissionJob> callback) {
        super("Submission Job");
        this.submitter = submitter;
        this.result = null;
        this.project = project;
        this.assigment = assignment;
        this.callback = callback;
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
            Display.getDefault().asyncExec(() -> {
                new AdvancedExceptionDialog("Submitting failed", ex).open(); // TODO: noch verbessern
            });

        } finally {
            lock.release();

        }
        return Status.OK_STATUS;
    }

}
