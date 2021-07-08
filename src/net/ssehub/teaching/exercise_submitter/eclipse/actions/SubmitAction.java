package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.submission.Submission;

public class SubmitAction extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window) {
        File location = project.getLocation().toFile();
        MessageDialog.openInformation(window.getShell(), "Exercise Submitter", "Selected Project: " + location);
        Submission submission = new Submission(project);
        submission.submit();
    }

}
