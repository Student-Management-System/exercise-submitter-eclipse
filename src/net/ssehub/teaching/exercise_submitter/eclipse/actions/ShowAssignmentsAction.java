package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;

/**
 * Shows all assignments and their current status to the user.
 * 
 * @author Lukas
 */
public class ShowAssignmentsAction extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        EclipseLog.info("Showing overview of all assignments");
        try {
            List<Assignment> assignments = Activator.getDefault().getManager().getAllAssignments();
            AssignmentDialog assDialog = new AssignmentDialog(EventHelper.getShell(event),
                    assignments, AssignmentDialog.Sorted.GROUPED);
            assDialog.open();
        } catch (NetworkException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e, "Failed to connect to student management system");
        } catch (AuthenticationException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e,
                    "Failed to authenticate to student management system");
        }
        return null;
    }

}
