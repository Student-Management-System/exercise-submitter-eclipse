package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.util.Optional;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.background.GetAssignmentsJob;
import net.ssehub.teaching.exercise_submitter.eclipse.background.ListVersionsJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentSelectionDialog;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;

/**
 * Shows all assignments and their current status to the user.
 * 
 * @author Lukas
 */
public class ShowAssignmentsAction extends AbstractActionUsingManager {

    @Override
    public void execute(ExecutionEvent event, ExerciseSubmitterManager manager) {
        
        new GetAssignmentsJob(EventHelper.getShell(event), manager, GetAssignmentsJob.NO_FILTER, assignments -> {
            
            AssignmentSelectionDialog assDialog = new AssignmentSelectionDialog(EventHelper.getShell(event),
                    assignments);
            assDialog.setButtonTexts("Show Version History", "Close");
            
            Optional<Assignment> assignment = assDialog.openAndGetSelectedAssignment();
            
            if (assignment.isPresent()) {
                if (manager.isReplayable(assignment.get())) {
                    Shell shell = EventHelper.getShell(event);
                    new ListVersionsJob(shell, manager, assignment.get(),
                            ListVersionsJob.displayVersionsCallback(shell, assignment.get().getName()))
                        .schedule();
                    
                } else {
                    MessageDialog.openInformation(EventHelper.getShell(event), "Assignment Not Accessible",
                            "The assignment " + assignment.get().getName() + " cannot currently be accessed.");
                }
            }
        }).schedule();
    }        

}
