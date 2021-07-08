package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;

/**
 * Shows all assignments and their current status to the user.
 * 
 * @author Lukas
 */
public class ShowAssignmentsAction extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        AssignmentDialog assDialog = new AssignmentDialog(EventHelper.getShell(event),
                Activator.getDefault().getManager().getAllAssignments(), AssignmentDialog.Sorted.GROUPED);
        assDialog.open();
        return null;
    }

}
