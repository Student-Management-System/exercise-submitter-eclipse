package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.submission.Submission;

public class SubmitAction extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window) {
        Submission submission = new Submission(project);
        submission.submit();
    }

}
