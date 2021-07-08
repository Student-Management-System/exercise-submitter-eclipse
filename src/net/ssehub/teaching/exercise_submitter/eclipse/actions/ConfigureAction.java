package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * Opens the configuration window.
 * 
 * @author Lukas
 */
public class ConfigureAction extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String id = "net.ssehub.teaching.exercise_submitter.eclipse.preferences.PreferencePage";
        PreferencesUtil.createPreferenceDialogOn(EventHelper.getShell(event), id, new String[] {id}, null).open();
        return null;
    }
    
}
