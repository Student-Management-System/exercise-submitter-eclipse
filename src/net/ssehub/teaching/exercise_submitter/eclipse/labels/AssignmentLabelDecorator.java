package net.ssehub.teaching.exercise_submitter.eclipse.labels;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import net.ssehub.teaching.exercise_submitter.eclipse.preferences.ProjectManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;

/**
 * Adds the associated {@link Assignment} names to project names.
 *
 * @see ProjectManager
 * 
 * @author Adam
 */
public class AssignmentLabelDecorator implements ILabelDecorator {
    
    @Override
    public void addListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }

    @Override
    public Image decorateImage(Image image, Object element) {
        return null;
    }

    @Override
    public String decorateText(String text, Object element) {
        IProject project = ((IAdaptable) element).getAdapter(IProject.class);
        
        String result = ProjectManager.INSTANCE.getStoredAssignmentName(project)
                .map(assignmentName ->  text + " submitted as: " + assignmentName)
                .orElse(null);
        
        return result;
    }

}
