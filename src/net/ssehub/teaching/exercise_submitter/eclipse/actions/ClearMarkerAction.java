package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers.EclipseMarker;

/**
 * Clears our markers from the selected project.
 * 
 * @author Lukas
 */
public class ClearMarkerAction extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window) {
        EclipseMarker.clearMarkerFromProjekt(project);
    }
    
}
