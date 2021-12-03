package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * A superclass for all actions that want to be executed on a single selected project.
 * 
 * @author Adam
 */
abstract class AbstractSingleProjectAction extends AbstractHandler {

    @Override
    public final Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = null;
        IStructuredSelection selection = null;

        if (event.getApplicationContext() instanceof IEvaluationContext) {
            IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();

            window = (IWorkbenchWindow) context.getVariable(ISources.ACTIVE_WORKBENCH_WINDOW_NAME);
            selection = (IStructuredSelection) context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
        }

        List<IProject> projects = new LinkedList<>();

        if (window != null) {
            if (selection != null) {
                for (Object selected : selection) {
                    if (selected instanceof IAdaptable) {
                        projects.add(((IAdaptable) selected).getAdapter(IProject.class));
                    }
                }
            }
        }

        if (projects.size() == 1) {
            execute(projects.get(0), window);
        } else if (projects.size() > 1) {
            MessageDialog.openError(window.getShell(), "Exercise Submitter", "Too many projects selected.");
        } else {
            MessageDialog.openError(window.getShell(), "Exercise Submitter", "No project selected.");
        }

        return null;
    }
    
    /**
     * Invoked if the action is triggered on a single selected project.
     * 
     * @param project The selected project.
     * @param window The active workbench window.
     */
    protected abstract void execute(IProject project, IWorkbenchWindow window);
    
}
