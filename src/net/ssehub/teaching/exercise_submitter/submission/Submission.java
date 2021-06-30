package net.ssehub.teaching.exercise_submitter.submission;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers.EclipseMarker;
import net.ssehub.teaching.exercise_submitter.exception.UserException;
import net.ssehub.teaching.exercise_submitter.lib.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.Assignment.State;
import net.ssehub.teaching.exercise_submitter.lib.Manager;
import net.ssehub.teaching.exercise_submitter.lib.Problem;
import net.ssehub.teaching.exercise_submitter.lib.SubmissionException;
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
			
			if(EclipseMarker.areMarkersInProjekt(project)) {
				boolean bResult =
						MessageDialog.openConfirm(new Shell(), "Exercise Submitter", "There are open errors/warnings. Continue?");

					if (!bResult) {
						return;
					}
					
			}	
			
			AssignmentDialog assDialog = new AssignmentDialog(new Shell(), manager.getAssignments(State.SUBMISSION));
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

			Submitter submitter = manager.getSubmitter(assignment);// verschiedene Hausaufgaben noch hinzufügen
			SubmissionResult sresult = submitter.submit(this.project.getLocation().toFile());

			if (sresult.isAccepted()) {
				for (Problem problem : sresult.getProblems()) {
					EclipseMarker.addMarker(problem.getFile().get(), problem.getMessage(), problem.getLine().get(),
							problem.getSeverity(), this.project); // noch nicht fertig dialogbox
					// NoSuchElementException.
				}

				// keine fehler
			} else {
				// fehler wurden gefunden

			}

		} catch (UserException | IllegalArgumentException | SubmissionException ex) {
			if (ex instanceof UserException) {
				((UserException) ex).show();
			} else {
				new AdvancedExceptionDialog("Submitting failed", ex).open(); // noch verbessern
			}
		}

	}
}
