package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

/**
 * Shows a help dialog to the user.
 * 
 * @author Lukas
 */
public class HelpAction extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Version version = FrameworkUtil.getBundle(this.getClass()).getVersion();
        MessageDialog.openInformation(EventHelper.getShell(event), "Exercise Submitter",
                "Current Version: " + version.toString());
        // TODO: more content
        return null;
    }

}
