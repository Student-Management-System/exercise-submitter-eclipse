package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.background.ListVersionsJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.eclipse.preferences.ProjectManager;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.GroupNotFoundException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;

/**
 * Action for display the version history for the selected Project.
 *
 * @author lukas
 *
 */
public class ViewVersionHistoryAction extends AbstractSingleProjectActionUsingManager {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window, ExerciseSubmitterManager manager) {

        Optional<Assignment> assignment = Optional.empty();
        
        try {
            assignment = ProjectManager.INSTANCE.getConnection(project, manager);
            
            if (assignment.isPresent()) {
                if (manager.isReplayable(assignment.get())) {
                    ListVersionsJob job = new ListVersionsJob(window.getShell(), manager, assignment.get(),
                            ListVersionsJob.displayVersionsCallback(window.getShell(), assignment.get().getName()));
                    job.schedule();
                    
                } else {
                    MessageDialog.openInformation(window.getShell(), "Assignment Not Accessible", "The assignment "
                            + assignment.get().getName() + " cannot currently be accessed.");
                }
                
            } else {
                MessageDialog.openInformation(window.getShell(), "No Assignment Connected", "The project "
                        + project.getName() + " has no connected assignment (it has not been submitted).\n\n"
                        + "Use the \"Show Assignments\" item in the menu bar to view the version history of "
                        + "other assignments.");
                
            }
        } catch (UserNotInCourseException e) {
            ExceptionDialogs.showUserNotInCourseDialog(manager.getCourse().getId());
        } catch (NetworkException e) {
            ExceptionDialogs.showNetworkExceptionDialog(e);
        } catch (AuthenticationException e) {
            ExceptionDialogs.showLoginFailureDialog();
        } catch (GroupNotFoundException e) {
            ExceptionDialogs.showUserNotInGroupDialog(assignment.map(Assignment::getName).orElse("unknown"));
        } catch (ApiException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Generic API exception");
        }
    }

}
