package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.util.Optional;

import org.eclipse.core.commands.ExecutionEvent;

import net.ssehub.teaching.exercise_submitter.eclipse.background.ReplayerJob;
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
 * Lets the user download a submission and creates a local project with the
 * content.
 *
 * @author Lukas
 */
public class DownloadSubmissionAction extends AbstractActionUsingManager {
    
    //TODO: remove selected dir 
    @Override
    public void execute(ExecutionEvent event, ExerciseSubmitterManager manager) {
        
        Optional<Assignment> selectedAssignment = Optional.empty();
        try {
            AssignmentSelectionDialog assDialog = new AssignmentSelectionDialog(EventHelper.getShell(event),
                    manager.getAllReplayableAssignments());
            
            selectedAssignment = assDialog.openAndGetSelectedAssignment();

            if (selectedAssignment.isPresent()) {
                EclipseLog.info("Starting download from Assignment: " + selectedAssignment.get());
                
                ReplayerJob job = new ReplayerJob(EventHelper.getShell(event),
                        manager.getReplayer(selectedAssignment.get()), selectedAssignment.get(), finishedJob -> { });
                job.setUser(true);
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
