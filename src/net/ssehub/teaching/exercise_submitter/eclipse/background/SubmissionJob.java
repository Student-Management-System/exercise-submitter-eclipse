package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.GroupNotFoundException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;
import net.ssehub.teaching.exercise_submitter.lib.submission.SubmissionException;
import net.ssehub.teaching.exercise_submitter.lib.submission.SubmissionResult;
import net.ssehub.teaching.exercise_submitter.lib.submission.Submitter;

/**
 * A job that submits a given project to a given assignment.
 * 
 * @author Adam
 * @author lukas
 */
public class SubmissionJob extends AbstractJob<SubmissionResult> {

    private ExerciseSubmitterManager manager;
    
    private Assignment assignment;
    
    private IProject project;
    
    /**
     * Creates a submission job.
     * 
     * @param shell The parent shell.
     * @param manager The manager to contact the student management system.
     * @param assignment The assignment to submit to.
     * @param project The project to submit.
     * @param callback The callback to call when the submission is done.
     */
    public SubmissionJob(Shell shell, ExerciseSubmitterManager manager, Assignment assignment, IProject project,
            Consumer<SubmissionResult> callback) {
        super("Submitting project " + project.getName() + " to " + assignment.getName(), shell, callback);
        this.manager = manager;
        this.assignment = assignment;
        this.project = project;
    }

    @Override
    protected Optional<SubmissionResult> run() {
        Optional<SubmissionResult> result = Optional.empty();
        
        try {
            Submitter submitter = this.manager.getSubmitter(this.assignment);
            
            result = Optional.of(submitter.submit(project.getLocation().toFile()));
            
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
        } catch (SubmissionException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Failed to submit");
        }
        
        return result;
    }

}
