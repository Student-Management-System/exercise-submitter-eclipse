package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.util.List;
import java.util.Optional;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.background.ListVersionsJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentSelectionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.GroupNotFoundException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;

/**
 * Shows all assignments and their current status to the user.
 * 
 * @author Lukas
 */
public class ShowAssignmentsAction extends AbstractActionUsingManager {

    @Override
    public void execute(ExecutionEvent event, ExerciseSubmitterManager manager) {
        EclipseLog.info("Showing overview of all assignments");
        
        Optional<Assignment> assignment = Optional.empty();
        
        try {
            List<Assignment> assignments = manager.getAllAssignments();
            AssignmentSelectionDialog assDialog = new AssignmentSelectionDialog(EventHelper.getShell(event),
                    assignments);
            assDialog.setButtonTexts("Show Version History", "Close");
            assignment = assDialog.openAndGetSelectedAssignment();
            
            if (assignment.isPresent()) {
                if (manager.isReplayable(assignment.get())) {
                    EclipseLog.info("Loading version log of assignment " + assignment.get().getName());
                    
                    Shell shell = EventHelper.getShell(event);
                    ListVersionsJob job = new ListVersionsJob(shell, manager, assignment.get(),
                            ListVersionsJob.displayVersionsCallback(shell, assignment.get().getName()));
                    job.schedule();
                    
                } else {
                    MessageDialog.openInformation(EventHelper.getShell(event), "Assignment Not Accessible",
                            "The assignment " + assignment.get().getName() + " cannot currently be accessed.");
                }
            }
            
        } catch (NetworkException e) {
            ExceptionDialogs.showNetworkExceptionDialog(e);
        } catch (UserNotInCourseException e) {
            ExceptionDialogs.showUserNotInCourseDialog(manager.getCourse().getId());
        } catch (GroupNotFoundException e) {
            ExceptionDialogs.showUserNotInGroupDialog(assignment.map(Assignment::getName).orElse("unknown"));
        } catch (AuthenticationException e) {
            ExceptionDialogs.showLoginFailureDialog();
        } catch (ApiException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Generic API exception");
        }
    }        

}
