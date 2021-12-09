package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.util.Optional;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.background.GetAssignmentsJob;
import net.ssehub.teaching.exercise_submitter.eclipse.background.ReplayJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentSelectionDialog;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;

/**
 * Lets the user download a submission and creates a local project with the content.
 *
 * @author Lukas
 * @author Adam
 */
public class DownloadSubmissionAction extends AbstractActionUsingManager {
    
    @Override
    public void execute(ExecutionEvent event, ExerciseSubmitterManager manager) {
        
        new GetAssignmentsJob(EventHelper.getShell(event), manager, manager::isReplayable, assignmentList -> {
            
            AssignmentSelectionDialog assDialog = new AssignmentSelectionDialog(EventHelper.getShell(event),
                    assignmentList);
            
            Optional<Assignment> selectedAssignment = assDialog.openAndGetSelectedAssignment();
            
            if (selectedAssignment.isPresent()) {
                Shell shell = EventHelper.getShell(event);
                new ReplayJob(shell, manager, selectedAssignment.get(),
                        project -> MessageDialog.openInformation(shell, "Submission Download",
                                "Submission has been downloaded into project " + project.getName()))
                    .schedule();
            }
                
        }).schedule();
    }
    
}
