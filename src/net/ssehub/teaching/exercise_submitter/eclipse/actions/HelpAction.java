package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

public class HelpAction extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Version version = FrameworkUtil.getBundle(this.getClass()).getVersion();
        MessageDialog.openInformation(new Shell(), "Exercise Submitter",
                "Current Version: " + version.toString() + "\r\n");
        return null;
    }

}
