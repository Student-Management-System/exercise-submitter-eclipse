package net.ssehub.teaching.exercise_submitter.submission;

import java.io.File;

import org.eclipse.core.resources.IProject;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.log.Errorlog;
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
			Assignment assignment = manager.getAssignments(State.SUBMISSION).get(0);
			Submitter submitter = manager.getSubmitter(assignment);// verschiedene Hausaufgaben noch hinzufügen
			SubmissionResult sresult = submitter.submit(this.project.getLocation().toFile());
			
			if(sresult.isAccepted()) {
				for(Problem problem : sresult.getProblems()) {
					EclipseMarker.addMarker(problem.getFile().get(), problem.getMessage(), 
							problem.getLine().get(),problem.getSeverity(),this.project); //noch nicht fertig dialogbox
					// NoSuchElementException.
				}
				//keine fehler
			} else {
				//fehler wurden gefunden
				for(Problem problem : sresult.getProblems()) {
					EclipseMarker.addMarker(problem.getFile().get(), problem.getMessage(), 
							problem.getLine().get(),problem.getSeverity(),this.project); //noch nicht fertig dialogbox
					// NoSuchElementException.
				}
				
				
			}
		
		} catch(Exception e) {
			System.out.println("Fehler");
		}
		
		
	}
}
