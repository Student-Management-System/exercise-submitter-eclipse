package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;

/**
 * Superclass for all actions that use the {@link ExerciseSubmitterManager}. The action method is only called if the
 * {@link ExerciseSubmitterManager} could be created (otherwise the creation attempt showed an error dialog already).
 * 
 * @author Adam
 */
abstract class AbstractActionUsingManager extends AbstractHandler {

    @Override
    public final Object execute(ExecutionEvent event) throws ExecutionException {
        Activator.getDefault().getManager().ifPresent(manager -> execute(event, manager));
        return null;
    }
    
    /**
     * The method that performs the action.
     *  
     * @param event The event that triggered the action.
     * @param manager The {@link ExerciseSubmitterManager} to use during the action.
     */
    protected abstract void execute(ExecutionEvent event, ExerciseSubmitterManager manager);

}
