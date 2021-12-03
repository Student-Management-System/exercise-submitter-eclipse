package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;

/**
 * A superclass for all actions that want to be executed on a single selected project and use the
 * {@link ExerciseSubmitterManager}.  The action method is only called if the {@link ExerciseSubmitterManager} could be
 * created (otherwise the creation attempt showed an error dialog already).
 * 
 * @author Adam
 */
abstract class AbstractSingleProjectActionUsingManager extends AbstractSingleProjectAction {

    @Override
    protected final void execute(IProject project, IWorkbenchWindow window) {
        Activator.getDefault().getManager().ifPresent(manager -> execute(project, window, manager));
    }
    
    /**
     * Invoked if the action is triggered on a single selected project.
     * 
     * @param project The selected project.
     * @param window The active workbench window.
     * @param manager The {@link ExerciseSubmitterManager} to use during the action.
     */
    protected abstract void execute(IProject project, IWorkbenchWindow window, ExerciseSubmitterManager manager);
    
}
