package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers.EclipseMarker;

public class ClearMarkerAction extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window) {
        try {
            project.deleteMarkers(EclipseMarker.MARKER_TYPE, false, IResource.DEPTH_INFINITE);
            MessageDialog.openInformation(window.getShell(), "Exercise Submitter", "Markers succesfully cleared");
        } catch (CoreException e) {
            // TODO
        }
    }
    
}
