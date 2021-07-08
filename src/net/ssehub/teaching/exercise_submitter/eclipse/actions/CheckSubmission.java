package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Checks if the selected project matches the submitted version. Shows differences of the two.
 * 
 * @author Adam
 */
public class CheckSubmission extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window) {
        MessageDialog.openWarning(window.getShell(), "Exercise Submitter", "Not yet implemented");
        // TODO: implement
    }

}
