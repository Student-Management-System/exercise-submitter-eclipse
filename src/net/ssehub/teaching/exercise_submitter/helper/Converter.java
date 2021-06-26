package net.ssehub.teaching.exercise_submitter.helper;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import net.ssehub.teaching.exercise_submitter.lib.Problem;
import net.ssehub.teaching.exercise_submitter.lib.Problem.Severity;

public class Converter {
	public static IMarker getIMarkerSeverity(Severity sev, IMarker im) {
		try {
			switch(sev) {
			case WARNING:
				im.setAttribute(IMarker.SEVERITY, IMarker.PRIORITY_NORMAL);
				break;
			case ERROR:
				im.setAttribute(IMarker.SEVERITY, IMarker.PRIORITY_HIGH);
				break;
			
			}
		} catch (CoreException e) {
			
		}
	
	return null; //exception
	}
}
