package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.background.CheckSubmissionJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.replay.ReplayException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;

/**
 * Checks if the selected project matches the submitted version. Shows differences of the two.
 * 
 * @author Lukas
 * @author Adam
 */
public class CheckSubmission extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window) {
        try {
            Assignment selectedAssignment = displayAssignmentDialog(window);
            
            EclipseLog.info("Starting download newest Version ");
            
            CheckSubmissionJob job = new CheckSubmissionJob(window.getShell(),
                    Activator.getDefault().getManager().getReplayer(selectedAssignment), selectedAssignment,
                    project.getLocation().toFile(), this::onCheckSubmissionFinished);
            job.setUser(true);
            job.schedule();
            
        } catch (NetworkException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e, "Failed to connect to student management system");
        } catch (AuthenticationException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e,
                    "Failed to authenticate to student management system");
        } catch (ApiException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e, "Generic API exception");
        } catch (ReplayException e) {
            MessageDialog.openError(window.getShell(), "Check Submission", e.getMessage());
        } catch (IllegalArgumentException | IOException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e, "IO Exception");
        } 
    }
    
    /**
     * Crates the assignmentdialog.
     * @param window , the current window
     * @return Assignment ,the selected assignment
     * @throws NetworkException
     * @throws AuthenticationException
     * @throws ApiException
     * @throws ReplayException
     */
    private Assignment displayAssignmentDialog(IWorkbenchWindow window)
            throws NetworkException, AuthenticationException, ApiException, ReplayException {
        
        List<Assignment> assignments = Activator.getDefault().getManager().getAllAssignments();
        AssignmentDialog assDialog = new AssignmentDialog(window.getShell(),
                assignments, AssignmentDialog.Sorted.NONE);
        assDialog.open();
        
        Assignment selectedassignment = null;
        
        if (assDialog.getSelectedAssignment().isPresent()) {
            selectedassignment = assDialog.getSelectedAssignment().get();
        } else {
            throw new ReplayException("No selected Assignment");
        }
        return selectedassignment;
    }
    /**
     * Called when check submission is finished.
     * @param job
     */
    private void onCheckSubmissionFinished(CheckSubmissionJob job) {
        //TODO: build dialog
        System.out.println("Version: " + job.getVersion().get());
        System.out.println("Result: " + job.getCheckResult().get());

    }

}
