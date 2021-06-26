package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.problemmarkers.EclipseMarker;
import net.ssehub.teaching.exercise_submitter.submission.Submission;

public class SubmitAction extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = null;
        IStructuredSelection selection = null;
        
        if (event.getApplicationContext() instanceof IEvaluationContext) {
            IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();
            
            window = (IWorkbenchWindow) context.getVariable(ISources.ACTIVE_WORKBENCH_WINDOW_NAME);
            selection = (IStructuredSelection) context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
        }
        
        List<IProject> projects = new LinkedList<>();
        
        if (window != null) {
            if (selection != null) {
                for (Object selected : selection) {
                    if (selected instanceof IAdaptable) {
                        projects.add(((IAdaptable) selected).getAdapter(IProject.class));
                    }
                }
            }
        }
        
        if (projects.size() == 1) {
            File location = projects.get(0).getLocation().toFile();
           // EclipseMarker.addMarker(projects.get(0).getFile("/src/test/test.java"), "Du hast versagt", 1);
            MessageDialog.openInformation(window.getShell(), "Exercise Submitter", "Selected Project: " + location);
            Submission submission = new Submission(projects.get(0));
            submission.submit();
        } else if (projects.size() > 1) {
            MessageDialog.openError(window.getShell(), "Exercise Submitter", "Too many projects selected.");
        } else {
            MessageDialog.openError(window.getShell(), "Exercise Submitter", "No project selected.");
        }
        
        return null;
    }

}
