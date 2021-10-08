package net.ssehub.teaching.exercise_submitter.eclipse.actions;


import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;


/**
 * Action for display the version history for the selected Project.
 * @author lukas
 *
 */
public class ViewVersionHistoryAction extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window)  {
        MessageDialog.openInformation(window.getShell(), "View Version History", "Not yet implemented");
        // TODO: more content
        
    }

}
