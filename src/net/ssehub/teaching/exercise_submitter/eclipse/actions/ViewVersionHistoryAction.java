package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

import net.ssehub.teaching.exercise_submitter.eclipse.background.ListVersionsJob;

public class ViewVersionHistoryAction extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window)  {
        MessageDialog.openInformation(window.getShell(), "View Version History", "Not yet implemented");
        // TODO: more content
        
    }

}
