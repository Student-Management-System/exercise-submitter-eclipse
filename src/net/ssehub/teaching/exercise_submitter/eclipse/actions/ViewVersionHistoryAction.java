package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.background.GetAssociatedAssignmentJob;
import net.ssehub.teaching.exercise_submitter.eclipse.background.ListVersionsJob;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;

/**
 * Action for display the version history for the selected Project.
 *
 * @author lukas
 * @author Adam
 */
public class ViewVersionHistoryAction extends AbstractSingleProjectActionUsingManager {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window, ExerciseSubmitterManager manager) {

        new GetAssociatedAssignmentJob(window.getShell(), manager, project, assignment -> {
            if (assignment.isPresent()) {
                if (manager.isReplayable(assignment.get())) {
                    new ListVersionsJob(window.getShell(), manager, assignment.get(),
                            ListVersionsJob.displayVersionsCallback(window.getShell(), assignment.get().getName()))
                        .schedule();
                    
                } else {
                    MessageDialog.openInformation(window.getShell(), "Assignment Not Accessible", "The assignment "
                            + assignment.get().getName() + " cannot currently be accessed.");
                }
                
            } else {
                MessageDialog.openInformation(window.getShell(), "No Assignment Associated", "The project "
                        + project.getName() + " has no associated assignment (it has not been submitted).\n\n"
                        + "Use the \"Show Assignments\" item in the menu bar to view the version history of "
                        + "other assignments.");
            }
        }).schedule();
    }

}
