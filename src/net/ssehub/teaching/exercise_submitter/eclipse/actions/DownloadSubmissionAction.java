package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Lets the user download a submission and creates a local project with the content.
 * 
 * @author Lukas
 */
public class DownloadSubmissionAction extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        MessageDialog.openWarning(new Shell(), "Exercise Submitter", "Not yet implemented");
        // TODO: implement
        return null;
    }

}
