package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;

/**
 * Shows all assignments and their current status to the user.
 * 
 * @author Lukas
 */
public class ShowAssignmentsAction extends AbstractActionUsingManager {

    @Override
    public void execute(ExecutionEvent event, ExerciseSubmitterManager manager) {
        EclipseLog.info("Showing overview of all assignments");
        
        try {
            List<Assignment> assignments = manager.getAllAssignments();
            AssignmentDialog assDialog = new AssignmentDialog(EventHelper.getShell(event),
                    assignments, AssignmentDialog.Sorted.GROUPED);
            assDialog.open();
        } catch (NetworkException e) {
            ExceptionDialogs.showNetworkExceptionDialog(e);
        } catch (AuthenticationException e) {
            ExceptionDialogs.showLoginFailureDialog();
        } catch (ApiException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Generic API exception");
        }
    }        

}
