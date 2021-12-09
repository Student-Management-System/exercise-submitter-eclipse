package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;

/**
 * A job that retrieves a list of assignments.
 * 
 * @author Adam
 */
public class GetAssignmentsJob extends AbstractJob<List<Assignment>> {

    public static final Predicate<Assignment> NO_FILTER = a -> true;
    
    private ExerciseSubmitterManager manager;
    
    private Predicate<Assignment> filter;
    
    /**
     * Creates a new job.
     * 
     * @param shell The parent shell that this job belongs to.
     * @param manager The manager to get a list of assignments.
     * @param filter A filter to apply to the assignment list; only the ones that pass this filter will be returned.
     * @param callback A callback to call with the list of (filtered) assignments.
     */
    public GetAssignmentsJob(Shell shell, ExerciseSubmitterManager manager, Predicate<Assignment> filter,
            Consumer<List<Assignment>> callback) {
        super("Retrieving assignment list for course " + manager.getCourse().getId(), shell, callback);
        this.manager = manager;
        this.filter = filter;
    }

    @Override
    protected Optional<List<Assignment>> run() {
        Optional<List<Assignment>> result = Optional.empty();
        
        try {
            result = Optional.of(this.manager.getAllAssignments().stream()
                    .filter(this.filter)
                    .collect(Collectors.toList()));
        } catch (NetworkException e) {
            ExceptionDialogs.showNetworkExceptionDialog(e);
        } catch (UserNotInCourseException e) {
            ExceptionDialogs.showUserNotInCourseDialog(manager.getCourse().getName());
        } catch (AuthenticationException e) {
            ExceptionDialogs.showLoginFailureDialog();
        } catch (ApiException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Generic API exception");
        }
        
        return result;
    }

}
