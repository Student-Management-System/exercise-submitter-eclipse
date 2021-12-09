package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.HelpDialog;

/**
 * Shows a help dialog to the user.
 * 
 * @author Lukas
 */
public class HelpAction extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        new HelpDialog(EventHelper.getShell(event)).open();
        return null;
    }
    
}
