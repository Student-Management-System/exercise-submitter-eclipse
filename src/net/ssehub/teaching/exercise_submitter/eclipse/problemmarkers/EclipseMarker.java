package net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import net.ssehub.teaching.exercise_submitter.eclipse.helper.Converter;
import net.ssehub.teaching.exercise_submitter.lib.Problem.Severity;

public class EclipseMarker {
    public static final String MARKER_TYPE = "net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers";

    public static void addMarker(File file, String message, int lineNumber, Severity sev, IProject project) {
        try {
            Pattern patt = Pattern.compile(project.getName());
            Matcher m = patt.matcher(file.getPath());
            m.find();
            IFile ifile = project.getFile(file.getPath().substring(m.end()));
            IMarker marker = ifile.createMarker(MARKER_TYPE);
            marker.setAttribute(IMarker.MESSAGE, message);
            Converter.getIMarkerSeverity(sev, marker);
            if (lineNumber == -1) {
                lineNumber = 1;
            }
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
        } catch (CoreException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addMarker(IFile file, String message, int lineNumber, Severity sev) { // für Kompatibilität
        try {
            IMarker marker = file.createMarker(MARKER_TYPE);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, IMarker.PRIORITY_HIGH);
            if (lineNumber == -1) {
                lineNumber = 1;
            }
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
        } catch (CoreException e) {
        }
    }

    public static void clearMarkerFromProjekt(IProject project) {
        try {
            project.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_INFINITE);
        } catch (CoreException e) {
        }
    }

    public static Boolean areMarkersInProjekt(IProject project) {
        boolean available = false;
        IMarker[] markers = null;
        try {
            markers = project.findMarkers(null, true, IResource.DEPTH_INFINITE);
            if (markers.length > 0) {
                available = true;
            }
        } catch (CoreException e) {
        }
        return available;
    }

}
