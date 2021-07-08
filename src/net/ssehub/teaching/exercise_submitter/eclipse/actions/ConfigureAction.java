package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class ConfigureAction extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String id = "net.ssehub.teaching.exercise_submitter.eclipse.preferences.PreferencePage";
        Shell shell = new Shell();
        PreferencesUtil.createPreferenceDialogOn(shell, id, new String[] { id }, null).open();
        return null;
    }
}
