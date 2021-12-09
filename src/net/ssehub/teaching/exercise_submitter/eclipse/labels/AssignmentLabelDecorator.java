package net.ssehub.teaching.exercise_submitter.eclipse.labels;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;

import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;

/**
 * Adds the associated {@link Assignment} names to project names.
 *
 * @see ProjectAssignmentMapper
 * 
 * @author Adam
 */
public class AssignmentLabelDecorator implements ILabelDecorator {
    
    private List<ILabelProviderListener> listeners = new LinkedList<>();
    
    @Override
    public void addListener(ILabelProviderListener listener) {
        this.listeners.add(listener);
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
        this.listeners.remove(listener);
    }

    @Override
    public Image decorateImage(Image image, Object element) {
        return null;
    }

    @Override
    public String decorateText(String text, Object element) {
        IProject project = ((IAdaptable) element).getAdapter(IProject.class);
        
        String result = ProjectAssignmentMapper.INSTANCE.getAssociatedAssignmentName(project)
                .map(assignmentName ->  text + " {" + assignmentName + "}")
                .orElse(null);
        
        return result;
    }

    /**
     * Updates the label on the given project. Should be called each time the label (potentially) changed.
     * 
     * @param project The project where the label should be updated.
     */
    public static void update(IProject project) {
        Display.getDefault().asyncExec(() -> {
            IDecoratorManager manager = PlatformUI.getWorkbench().getDecoratorManager();
            
            AssignmentLabelDecorator decorator = (AssignmentLabelDecorator)
                    manager.getLabelDecorator("net.ssehub.teaching.exercise_submitter.eclipse.assignmentDecorator");
            
            if (decorator != null) {
                LabelProviderChangedEvent event = new LabelProviderChangedEvent(decorator, project);
                decorator.listeners.forEach(listener -> listener.labelProviderChanged(event));
            }
        });
    }

}
