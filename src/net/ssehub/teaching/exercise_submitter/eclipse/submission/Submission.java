package net.ssehub.teaching.exercise_submitter.eclipse.submission;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.background.SubmissionJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.exception.UserException;
import net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers.EclipseMarker;
import net.ssehub.teaching.exercise_submitter.lib.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.Assignment.State;
import net.ssehub.teaching.exercise_submitter.lib.Manager;
import net.ssehub.teaching.exercise_submitter.lib.Problem;
import net.ssehub.teaching.exercise_submitter.lib.SubmissionResult;
import net.ssehub.teaching.exercise_submitter.lib.Submitter;

public class Submission {

    private IProject project;

    public Submission(IProject project) {
        this.project = project;
    }

    public void submit() {
        try {
            Manager manager = Activator.getEclipseManager().getManager();
            EclipseMarker.clearMarkerFromProjekt(this.project);

            if (EclipseMarker.areMarkersInProjekt(this.project)) {
                boolean bResult = MessageDialog.openConfirm(new Shell(), "Exercise Submitter",
                        "There are open errors/warnings. Continue?");

                if (!bResult) {
                    return;
                }

            }

            AssignmentDialog assDialog = new AssignmentDialog(new Shell(), manager.getAssignments(State.SUBMISSION),
                    AssignmentDialog.Sorted.NONE);
            int iResult = assDialog.open();

            Assignment assignment = null;

            if (iResult == 0) {
                if (assDialog.getSelectedAssignment() != null) {
                    assignment = assDialog.getSelectedAssignment();
                } else {
                    // nichts ausgewählt
                    // throw something
                    throw new UserException(UserException.EXCEPTION_LIST_NOTSELECTED);
                }

            } else {
                // cancel
                return;
            }

            Submitter submitter = manager.getSubmitter(assignment); // verschiedene Hausaufgaben noch hinzufügen

            SubmissionJob sj = new SubmissionJob(submitter, this.project, assignment);
            sj.setUser(true);
            sj.schedule();

        } catch (UserException | IllegalArgumentException ex) {
            if (ex instanceof UserException) {
                ((UserException) ex).show();
            } else {
                new AdvancedExceptionDialog("Submitting failed", ex).open(); // noch verbessern
            }
        }

    }

    public static void jobIsDone(SubmissionResult sresult, IProject project, Assignment assignment) {
        
        String mainMessage;
        int dialogType;
        
        if (sresult.isAccepted()) {
            mainMessage = "Your project " + project.getName() + " was successfully submitted to assignment "
                    + assignment.getName() + ".";
            dialogType = MessageDialog.INFORMATION;
        } else {
            mainMessage = "Your submission of project " + project.getName() + " to assignment " + assignment.getName()
                + " was NOT accepted.";
            dialogType = MessageDialog.ERROR;
        }

        
        int numErrors = 0;
        int numWarnings = 0;
        for (Problem problem : sresult.getProblems()) {
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
                    problem.getSeverity(), project); // noch nicht fertig dialogbox
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
