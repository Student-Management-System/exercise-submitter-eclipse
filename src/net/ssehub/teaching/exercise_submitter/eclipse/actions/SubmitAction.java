package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.io.File;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.background.SubmissionJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers.EclipseMarker;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.submission.Problem;
import net.ssehub.teaching.exercise_submitter.lib.submission.Submitter;

/**
 * Submits the selected project. Lets the user choose which assignment the project should be submitted to.
 * 
 * @author Adam
 * @author Lukas
 */
public class SubmitAction extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window) {
        EclipseLog.info("Starting submision of project " + project.getName());
        
        EclipseMarker.clearMarkerFromProjekt(project);
        
        if (EclipseMarker.areMarkersInProjekt(project)) {
            boolean bResult = MessageDialog.openConfirm(window.getShell(), "Exercise Submitter",
                    "There are open errors/warnings in the selected project.\n\nContinue?");

            if (!bResult) {
                EclipseLog.info("Submission aborted by user due to errors/warnings in project");
                return;
            }
        }
        
        ExerciseSubmitterManager manager = Activator.getDefault().getManager();
        
        EclipseLog.info("Showing assignment selector to user");
        Optional<Assignment> assignment = chooseAssignment(window, manager);
        
        if (assignment.isPresent()) {
            EclipseLog.info("User selected assignment " + assignment.get().getName());
            
            Submitter submitter = manager.getSubmitter(assignment.get());
            // TODO: verschiedene Hausaufgaben noch hinzufügen?

            EclipseLog.info("Starting submission job");
            SubmissionJob sj = new SubmissionJob(submitter, project, assignment.get(), window.getShell(),
                    this::onSubmissionFinished);
            sj.setUser(true);
            sj.schedule();
            
        } else {
            EclipseLog.info("User canceled at assignment selection");
        }
    }
    
    /**
     * Lets the user choose an assignment.
     * 
     * @param window The window to show the dialog for.
     * @param manager The {@link ExerciseSubmitterManager} to get {@link Assignment}s from.
     * 
     * @return The assignment selected by the user. Empty if the user canceled.
     */
    private Optional<Assignment> chooseAssignment(IWorkbenchWindow window, ExerciseSubmitterManager manager) {
        Optional<Assignment> selected = Optional.empty();
        
        try {
            AssignmentDialog assDialog = new AssignmentDialog(window.getShell(), manager.getAllSubmittableAssignments(),
                    AssignmentDialog.Sorted.NONE);
            
            int dialogResult;
            
            do {
                dialogResult = assDialog.open();
                selected = assDialog.getSelectedAssignment();
                
            } while (dialogResult == 0 && selected.isEmpty());
            
        } catch (NetworkException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e, "Failed to connect to student management system");
        } catch (AuthenticationException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e,
                    "Failed to authenticate to student management system");
        }
        
        return selected;
    }
    
    /**
     * Callback that is called when the {@link SubmissionJob} has finished. This is always called, even when the
     * submission failed.
     * <p>
     * Displays the submission result to the user.
     * 
     * @param job The {@link SubmissionJob} that finished.
     */
    private void onSubmissionFinished(SubmissionJob job) {
        EclipseLog.info("Submission job finished (project " + job.getProject().getName() + ")");
        
        String mainMessage;
        int dialogType;
        
        if (job.getSubmissionResult().isAccepted()) {
            EclipseLog.info("Submission was accepted");
            mainMessage = "Your project " + job.getProject().getName() + " was successfully submitted to assignment "
                    + job.getAssigment().getName() + ".";
            dialogType = MessageDialog.INFORMATION;
            
        } else {
            EclipseLog.info("Submission was not accepted");
            mainMessage = "Your submission of project " + job.getProject().getName() + " to assignment "
                    + job.getAssigment().getName() + " was NOT accepted.";
            dialogType = MessageDialog.ERROR;
        }

        EclipseLog.info("Adding " + job.getSubmissionResult().getProblems().size() + " problem markers");
        int numErrors = 0;
        int numWarnings = 0;
        for (Problem problem : job.getSubmissionResult().getProblems()) {
            switch (problem.getSeverity()) {
            case WARNING:
                numWarnings++;
                break;
            case ERROR:
            default:
                numErrors++;
                break;
            }
            EclipseMarker.addMarker(problem.getFile().orElse(new File(".project")), problem.getMessage(),
                    problem.getLine().orElse(-1),
                    problem.getSeverity(), job.getProject()); // TODO: noch nicht fertig dialogbox
        }
        
        String problemsMessage = createProblemMessage(numErrors, numWarnings);
        
        MessageDialog.open(dialogType, job.getShell(), "Exercise Submitter", mainMessage + "\n\n" + problemsMessage,
                dialogType);
    }
    
    /**
     * Creates a message describing the number of problems. Empty if on problems are present.
     * 
     * @param numErrors The number of problems of type error.
     * @param numWarnings The number of problems of type warning.
     * 
     * @return A message describing the number of errors and warnings.
     */
    private String createProblemMessage(int numErrors, int numWarnings) {
        String problemsMessage = "";
        if (numErrors > 0 && numWarnings > 0) {
            problemsMessage = numErrors + " errors and " + numWarnings + " warnings were found in your submission.\n"
                    + "Problem markers have been added to your project.";
            
        } else if (numErrors > 0) {
            problemsMessage = numErrors + " errors were found in your submission.\n"
                    + "Problem markers have been added to your project.";
            
        } else if (numWarnings > 0) {
            problemsMessage = numWarnings + " warnings were found in your submission.\n"
                    + "Problem markers have been added to your project.";
        }
        return problemsMessage;
    }

}
