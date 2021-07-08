package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;

public class ShowAssignmentsAction extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        AssignmentDialog assDialog = new AssignmentDialog(new Shell(),
                Activator.getEclipseManager().getManager().getAllAssignments(), AssignmentDialog.Sorted.GROUPED);
        int result = assDialog.open();
        return null;
    }
}
