package net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import net.ssehub.teaching.exercise_submitter.lib.Problem.Severity;

/**
 * Utility methods for working with markers in projects.
 * 
 * @author Lukas
 * @author Adam
 */
public class EclipseMarker {
    
    private static final String MARKER_TYPE = "net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers";

    /**
     * No instances allowed.
     */
    private EclipseMarker() {
    }
    
    /**
     * Creates a marker in the given location.
     * 
     * @param file The file inside the project to create the marker in. Must be relative to the project root.
     * @param message The message of the marker.
     * @param lineNumber The line number of the marker.
     * @param sev The severity of the marker.
     * @param project The project to add the marker in.
     */
    public static void addMarker(File file, String message, int lineNumber, Severity sev, IProject project) {
        try {
            IFile ifile = project.getFile(file.getPath());
            IMarker marker = ifile.createMarker(MARKER_TYPE);
            marker.setAttribute(IMarker.MESSAGE, message);
            
            if (sev == Severity.WARNING) {
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
            } else {
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            }
            
            if (lineNumber == -1) {
                lineNumber = 1;
            }
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
        } catch (CoreException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Clears all our markers from the given project.
     * 
     * @param project The project to clear all our markers from.
     */
    public static void clearMarkerFromProjekt(IProject project) {
        try {
            project.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_INFINITE);
        } catch (CoreException e) {
        }
    }

    /**
     * Checks if there are any warning or error markers at all (including ones not by us) in the given project.
     * 
     * @param project The project to search markers in.
     * 
     * @return Whether there are any error or warning markers in the project.
     */
    public static boolean areMarkersInProjekt(IProject project) {
        boolean available = false;
        try {
            available = Arrays.stream(project.findMarkers(null, true, IResource.DEPTH_INFINITE))
                .filter(marker ->
                        marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR) == IMarker.SEVERITY_WARNING
                        || marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR) == IMarker.SEVERITY_ERROR)
                .count() > 0;
        } catch (CoreException e) {
        }
        return available;
    }

}
