package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.background.SubmissionJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentSelectionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.labels.ProjectAssignmentMapper;
import net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers.EclipseMarker;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.submission.Problem;
import net.ssehub.teaching.exercise_submitter.lib.submission.SubmissionResult;

/**
 * Submits the selected project. Lets the user choose which assignment the project should be submitted to.
 *
 * @author Adam
 * @author Lukas
 */
public class SubmitAction extends AbstractSingleProjectActionUsingManager {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window, ExerciseSubmitterManager manager) {
        
        EclipseMarker.clearMarkerFromProject(project);

        if (EclipseMarker.areMarkersInProject(project)) {
            boolean bResult = MessageDialog.openConfirm(window.getShell(), "Exercise Submitter",
                    "There are open errors/warnings in the selected project.\n\nContinue?");

            if (!bResult) {
                return;
            }
        }

        AssignmentSelectionDialog.selectAssignmentWithAssociated(project, window, manager, manager::isSubmittable,
                selectedAssignment -> {
                    if (selectedAssignment.isPresent()) {
                        Assignment assignment = selectedAssignment.get();
                        
                        new SubmissionJob(window.getShell(), manager, assignment, project,
                                (submissionResult) -> {
                                    createSubmissionFinishedDialog(window.getShell(), project, assignment,
                                            submissionResult);
                                }).schedule();
                    }
                });
    }

    /**
     * Creates a dialog that informs the user of the result of the submission.
     *
     * @param shell The parent shell to open the dialog for.
     * @param project The project that was submitted.
     * @param assignment The assignment that was submitted.
     * @param result The result of the submission.
     */
    public static void createSubmissionFinishedDialog(Shell shell, IProject project, Assignment assignment,
            SubmissionResult result) {
        
        if (result.isAccepted()) {
            ProjectAssignmentMapper.INSTANCE.setAssociation(project, assignment);
        }

        String mainMessage;
        int dialogType;

        if (result.isAccepted()) {
            mainMessage = "Your project " + project.getName() + " was successfully submitted to assignment "
                    + assignment.getName() + ".";
            dialogType = MessageDialog.INFORMATION;

        } else {
            String sameSubmission = result.getProblems().get(0).getMessage()
                    .equals("Submission is the same as the previous one") 
                    ? " \n Submission is the same as the previous one" : "";
            mainMessage = "Your submission of project " + project.getName() + " to assignment "
                    + assignment.getName() + " was NOT accepted." + sameSubmission;
            dialogType = MessageDialog.ERROR;
        }

        int numErrors = 0;
        int numWarnings = 0;
        for (Problem problem : result.getProblems()) {
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
                    problem.getLine().orElse(-1), problem.getSeverity(), project);
        }

        String problemsMessage = createProblemMessage(numErrors, numWarnings);

        MessageDialog.open(dialogType, shell, "Submission Result", mainMessage + "\n\n" + problemsMessage,
                dialogType);
    }

    /**
     * Creates a message describing the number of problems. Empty if on problems are
     * present.
     *
     * @param numErrors   The number of problems of type error.
     * @param numWarnings The number of problems of type warning.
     *
     * @return A message describing the number of errors and warnings.
     */
    public static String createProblemMessage(int numErrors, int numWarnings) {
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
