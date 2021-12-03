package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.io.File;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.osgi.service.prefs.BackingStoreException;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.background.SubmissionJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.eclipse.preferences.ProjectManagerException;
import net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers.EclipseMarker;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.GroupNotFoundException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;
import net.ssehub.teaching.exercise_submitter.lib.submission.Problem;
import net.ssehub.teaching.exercise_submitter.lib.submission.Submitter;

/**
 * Submits the selected project. Lets the user choose which assignment the
 * project should be submitted to.
 *
 * @author Adam
 * @author Lukas
 */
public class SubmitAction extends AbstractSingleProjectActionUsingManager {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window, ExerciseSubmitterManager manager) {
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

        Optional<Assignment> assignment = Optional.empty();

        try {
            Assignment savedAssignment = Activator.getDefault().getProjectManager().getConnection(project, manager);
            boolean questionResult = MessageDialog.openQuestion(window.getShell(), "Submit",
                    savedAssignment.getName() + " is connected. Do you want to submit to this assignment or change it ?"
                            + " \n\n Yes = keep \n No = Change assignment");
            if (questionResult) {
                assignment = Optional.ofNullable(savedAssignment);
            } else {
                EclipseLog.info("Showing assignment selector to user");
                assignment = this.chooseAssignment(window, manager);
            }
        } catch (ProjectManagerException e) {
            EclipseLog.info("Showing assignment selector to user");
            assignment = this.chooseAssignment(window, manager);
        }

        if (assignment.isPresent()) {
            EclipseLog.info("User selected assignment " + assignment.get().getName());

            try {
                Submitter submitter = manager.getSubmitter(assignment.get());

                EclipseLog.info("Starting submission job");
                SubmissionJob sj = new SubmissionJob(submitter, project, assignment.get(), window.getShell(),
                        this::onSubmissionFinished);
                sj.setUser(true);
                sj.schedule();

            } catch (UserNotInCourseException e) {
                ExceptionDialogs.showUserNotInCourseDialog(manager.getCourse().getId());
            } catch (NetworkException e) {
                ExceptionDialogs.showNetworkExceptionDialog(e);
            } catch (AuthenticationException e) {
                ExceptionDialogs.showLoginFailureDialog();
            } catch (GroupNotFoundException e) {
                ExceptionDialogs.showUserNotInGroupDialog(assignment.get().getName());
            } catch (ApiException e) {
                ExceptionDialogs.showUnexpectedExceptionDialog(e, "Generic API exception");
            }
            // TODO: verschiedene Hausaufgaben noch hinzufügen?

        } else {
            EclipseLog.info("User canceled at assignment selection");
        }
    }

    /**
     * Lets the user choose an assignment.
     *
     * @param window  The window to show the dialog for.
     * @param manager The {@link ExerciseSubmitterManager} to get
     *                {@link Assignment}s from.
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
                // only use selected Assignment if user press ok
                if (dialogResult == Window.OK) {
                    selected = assDialog.getSelectedAssignment();
                }

                // user press ok without selecting anything. -> Retry
            } while (dialogResult == Window.OK && selected.isEmpty());

        } catch (NetworkException e) {
            ExceptionDialogs.showNetworkExceptionDialog(e);
        } catch (AuthenticationException e) {
            ExceptionDialogs.showLoginFailureDialog();
        } catch (ApiException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Generic API exception");
        }

        return selected;
    }

    /**
     * Callback that is called when the {@link SubmissionJob} has finished. This is
     * always called, even when the submission failed.
     * <p>
     * Displays the submission result to the user.
     *
     * @param job The {@link SubmissionJob} that finished.
     */
    private void onSubmissionFinished(SubmissionJob job) {
        if (job.getSubmissionResult().isAccepted()) {
            try {
                Activator.getDefault().getProjectManager().setConnection(job.getProject(), job.getAssigment());
            } catch (BackingStoreException e) {
                Display.getDefault().syncExec(() -> {
                    ExceptionDialogs.showUnexpectedExceptionDialog(e, "Cant save settings");
                });
            }
        }
        createSubmissionFinishedDialog(job);
    }

    /**
     * Creates submissionFinishedDialog.
     *
     * @param job , finishedjob
     */
    public static void createSubmissionFinishedDialog(SubmissionJob job) {
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
                    problem.getLine().orElse(-1), problem.getSeverity(), job.getProject()); // TODO: noch nicht fertig
                                                                                            // dialogbox
        }

        String problemsMessage = createProblemMessage(numErrors, numWarnings);

        MessageDialog.open(dialogType, job.getShell(), "Exercise Submitter", mainMessage + "\n\n" + problemsMessage,
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
