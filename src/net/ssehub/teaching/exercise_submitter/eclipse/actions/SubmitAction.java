package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.io.File;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.background.SubmissionJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers.EclipseMarker;
import net.ssehub.teaching.exercise_submitter.lib.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.Assignment.State;
import net.ssehub.teaching.exercise_submitter.lib.Manager;
import net.ssehub.teaching.exercise_submitter.lib.Problem;
import net.ssehub.teaching.exercise_submitter.lib.Submitter;

/**
 * Submits the selected project. Lets the user choose which assignment the project should be submitted to.
 * 
 * @author Adam
 * @author Lukas
 */
public class SubmitAction extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window) {
        EclipseMarker.clearMarkerFromProjekt(project);
        
        if (EclipseMarker.areMarkersInProjekt(project)) {
            boolean bResult = MessageDialog.openConfirm(window.getShell(), "Exercise Submitter",
                    "There are open errors/warnings in the selected project.\n\nContinue?");

            if (!bResult) {
                return;
            }
        }
        
        Manager manager = Activator.getEclipseManager().getManager();
        
        Optional<Assignment> assignment = chooseAssignment(window, manager);
        
        if (assignment.isPresent()) {
            Submitter submitter = manager.getSubmitter(assignment.get()); // verschiedene Hausaufgaben noch hinzufügen

            SubmissionJob sj = new SubmissionJob(submitter, project, assignment.get(), this::onSubmissionFinished);
            sj.setUser(true);
            sj.schedule();
        }
    }
    
    /**
     * Lets the user choose an assignment.
     * 
     * @param window The window to show the dialog for.
     * @param manager The {@link Manager} to get {@link Assignment}s from.
     * 
     * @return The assignment selected by the user. Empty if the user canceled.
     */
    private Optional<Assignment> chooseAssignment(IWorkbenchWindow window, Manager manager) {
        AssignmentDialog assDialog = new AssignmentDialog(window.getShell(), manager.getAssignments(State.SUBMISSION),
                AssignmentDialog.Sorted.NONE);
        
        int dialogResult;
        Optional<Assignment> selected = Optional.empty();
        
        do {
            dialogResult = assDialog.open();
            selected = Optional.ofNullable(assDialog.getSelectedAssignment());
            
        } while (dialogResult == 0 && selected.isEmpty());

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
        
        String mainMessage;
        int dialogType;
        
        if (job.getSubmissionResult().isAccepted()) {
            mainMessage = "Your project " + job.getProject().getName() + " was successfully submitted to assignment "
                    + job.getAssigment().getName() + ".";
            dialogType = MessageDialog.INFORMATION;
        } else {
            mainMessage = "Your submission of project " + job.getProject().getName() + " to assignment "
                    + job.getAssigment().getName() + " was NOT accepted.";
            dialogType = MessageDialog.ERROR;
        }

        
        int numErrors = 0;
        int numWarnings = 0;
        for (Problem problem : job.getSubmissionResult().getProblems()) {
            switch (problem.getSeverity()) {
            case ERROR:
                numErrors++;
                break;
            case WARNING:
                numWarnings++;
                break;
            default:
                break;
            }
            EclipseMarker.addMarker(problem.getFile().orElse(new File(".project")), problem.getMessage(),
                    problem.getLine().orElse(-1),
                    problem.getSeverity(), job.getProject()); // noch nicht fertig dialogbox
        }
        
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
        
        MessageDialog.open(dialogType, new Shell(), "Exercise Submitter", mainMessage + "\n\n" + problemsMessage,
                dialogType);
    }

}
