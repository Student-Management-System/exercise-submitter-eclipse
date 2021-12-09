package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.background.CheckSubmissionJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentSelectionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.GroupNotFoundException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;

/**
 * Checks if the selected project matches the submitted version.
 *
 * @author Lukas
 * @author Adam
 */
public class CheckSubmission extends AbstractSingleProjectActionUsingManager {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window, ExerciseSubmitterManager manager) {

        Optional<Assignment> selectedAssignment = Optional.empty();
        try {
            selectedAssignment = AssignmentSelectionDialog.selectAssignmentWithAssociated(project, window, manager,
                manager::isReplayable);
            
            if (selectedAssignment.isPresent()) {
                CheckSubmissionJob job = new CheckSubmissionJob(window.getShell(), manager, selectedAssignment.get(),
                        project);
                job.schedule();
            }
            
        } catch (NetworkException e) {
            ExceptionDialogs.showNetworkExceptionDialog(e);
        } catch (UserNotInCourseException e) {
            ExceptionDialogs.showUserNotInCourseDialog(manager.getCourse().getId());
        } catch (GroupNotFoundException e) {
            ExceptionDialogs.showUserNotInGroupDialog(selectedAssignment.map(Assignment::getName).orElse("unknown"));
        } catch (AuthenticationException e) {
            ExceptionDialogs.showLoginFailureDialog();
        } catch (ApiException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Generic API exception");
        }
    }

}
