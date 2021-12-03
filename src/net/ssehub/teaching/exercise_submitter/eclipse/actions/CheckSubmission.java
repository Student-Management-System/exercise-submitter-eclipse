package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.background.CheckSubmissionJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.CheckSubmissionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.eclipse.preferences.ProjectManagerException;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.replay.ReplayException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;

/**
 * Checks if the selected project matches the submitted version. Shows
 * differences of the two.
 *
 * @author Lukas
 * @author Adam
 */
public class CheckSubmission extends AbstractSingleProjectActionUsingManager {

    private IProject project;

    @Override
    protected void execute(IProject project, IWorkbenchWindow window, ExerciseSubmitterManager manager) {

        this.project = project;

        try {
            
            Optional<Assignment> selectedAssignment = chooseAssignment(project, window, manager);
            
            if (selectedAssignment.isEmpty()) {
                throw new ReplayException("No assignment is selected");
            }

            EclipseLog.info("Starting download newest Version ");

            CheckSubmissionJob job = new CheckSubmissionJob(window.getShell(),
                    manager.getReplayer(selectedAssignment.get()), selectedAssignment.get(),
                    project.getLocation().toFile(), finishedJob -> onCheckSubmissionFinished(finishedJob, manager));
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
        }
    }
    /**
     * This method handles the selection process for the assignment.
     * @param project
     * @param window
     * @param manager 
     * @return Optional<Assignment>
     * @throws ApiException
     * @throws ReplayException
     */
    private Optional<Assignment> chooseAssignment(IProject project, IWorkbenchWindow window,
            ExerciseSubmitterManager manager) throws ApiException, ReplayException {
       
        Assignment assignment = null;
        try {
            Assignment savedAssignment = Activator.getDefault().getProjectManager().getConnection(project, manager);
            
            int chosen = MessageDialog.open(MessageDialog.QUESTION, window.getShell(), "Choose Assignment",
                    "This project was last submitted to " + savedAssignment.getName() + ". Submit to this again?",
                    SWT.NONE, savedAssignment.getName(), "Different Assignment");
            
            System.out.println(chosen);
            
            if (chosen == 0) {
                assignment = savedAssignment;
            } else if (chosen == 1) {
                EclipseLog.info("Showing assignment selector to user");
                assignment = displayAssignmentDialog(window, manager);
            }
        } catch (ProjectManagerException ex) {
            EclipseLog.info("Showing assignment selector to user");
            assignment = displayAssignmentDialog(window, manager);
        }
        
        return Optional.ofNullable(assignment);
    }

    /**
     * Crates the assignmentdialog.
     *
     * @param window , the current window
     * @param manager 
     * @return Assignment ,the selected assignment
     * @throws NetworkException
     * @throws AuthenticationException
     * @throws ApiException
     * @throws ReplayException
     */
    private Assignment displayAssignmentDialog(IWorkbenchWindow window, ExerciseSubmitterManager manager)
            throws NetworkException, AuthenticationException, ApiException, ReplayException {

        List<Assignment> assignments = manager.getAllSubmittableAssignments();
        AssignmentDialog assDialog = new AssignmentDialog(window.getShell(), assignments, AssignmentDialog.Sorted.NONE);
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
     *
     * @param job
     * @param manager
     */
    private void onCheckSubmissionFinished(CheckSubmissionJob job, ExerciseSubmitterManager manager) {
        // TODO: build dialog

        CheckSubmissionDialog dialog = new CheckSubmissionDialog(job.getShell(), job.getVersionlist(),
                manager, this.project, job.getCheckResult());
        dialog.open();

    }

}
