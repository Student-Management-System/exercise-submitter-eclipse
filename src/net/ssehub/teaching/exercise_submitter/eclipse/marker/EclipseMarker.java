package net.ssehub.teaching.exercise_submitter.eclipse.marker;



import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class EclipseMarker {
	public static final String MARKER_TYPE = "net.ssehub.teaching.exercise_submitter.eclipse.marker";
	
	public static void addMarker(IFile file, String message, int lineNumber) {
		  try {
	             IMarker marker = file.createMarker(MARKER_TYPE);
	             marker.setAttribute(IMarker.MESSAGE, message);
	             marker.setAttribute(IMarker.SEVERITY, IMarker.PRIORITY_HIGH);
	             if (lineNumber == -1) {
	                lineNumber = 1;
	             }
	             marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
	        }
	        catch (CoreException e) {}
	}
	
	public static void clearMarkerFromProjekt(IProject project) {
		try {
		     project.deleteMarkers(MARKER_TYPE , false, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {}
		}
	
	}
