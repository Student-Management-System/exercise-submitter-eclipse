package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.background.CheckSubmissionJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentSelectionDialog;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;

/**
 * Checks if the selected project matches the submitted version.
 *
 * @author Lukas
 * @author Adam
 */
public class CheckSubmission extends AbstractSingleProjectActionUsingManager {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window, ExerciseSubmitterManager manager) {
        AssignmentSelectionDialog.selectAssignmentWithAssociated(project, window, manager, manager::isReplayable,
                selectedAssignment -> {
                    if (selectedAssignment.isPresent()) {
                        new CheckSubmissionJob(window.getShell(), manager, selectedAssignment.get(), project)
                            .schedule();
                    }
                });
    }

}
