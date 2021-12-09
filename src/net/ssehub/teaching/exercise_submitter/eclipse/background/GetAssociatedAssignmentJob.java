package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.eclipse.labels.ProjectAssignmentMapper;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;

/**
 * A job that retrieves the assignment associated with a project.
 * 
 * @see ProjectAssignmentMapper
 * 
 * @author Adam
 */
public class GetAssociatedAssignmentJob extends AbstractJob<Optional<Assignment>> {

    
    private ExerciseSubmitterManager manager;
    
    private IProject project;
    
    /**
     * Creates a job.
     * 
     * @param shell The parent shell that this job is for.
     * @param manager The manager to retrieve assignments with.
     * @param project The project to get the associated assignment for.
     * @param callback The callback for the associated assignment. Will get {@link Optional#empty()} if no assignment
     *      is associated with the given project.
     */
    public GetAssociatedAssignmentJob(Shell shell, ExerciseSubmitterManager manager, IProject project,
            Consumer<Optional<Assignment>> callback) {
        super("Retrieving associated assignment for project " + project.getName(), shell, callback);
        this.manager = manager;
        this.project = project;
    }

    @Override
    protected Optional<Optional<Assignment>> run() {
        Optional<Optional<Assignment>> result = Optional.empty();
        
        try {
            result = Optional.of(ProjectAssignmentMapper.INSTANCE.getAssociatedAssignment(project, manager));
            
        } catch (NetworkException e) {
            ExceptionDialogs.showNetworkExceptionDialog(e);
        } catch (UserNotInCourseException e) {
            ExceptionDialogs.showUserNotInCourseDialog(manager.getCourse().getId());
        } catch (AuthenticationException e) {
            ExceptionDialogs.showLoginFailureDialog();
        } catch (ApiException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Generic API exception");
        }
        
        return result;
    }

}
