package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.background.ListVersionsJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.preferences.ProjectManagerException;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;

/**
 * Action for display the version history for the selected Project.
 *
 * @author lukas
 *
 */
public class ViewVersionHistoryAction extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window) {

        Assignment assignment;
        try {
            assignment = Activator.getDefault().getProjectManager().getConnection(project);
            ListVersionsJob job;

            job = new ListVersionsJob(window.getShell(), Activator.getDefault().getManager().getReplayer(assignment),
                    assignment, this::onListVersionFinished);
            job.setUser(true);
            job.schedule();
        } catch (ProjectManagerException | IllegalArgumentException | ApiException | IOException e) {
            if (e instanceof ProjectManagerException) {
                if (e.getMessage().equals(ProjectManagerException.NOTAVAILABLE)
                        || e.getMessage().equals(ProjectManagerException.NOTCONNECTED)) {
                    MessageDialog.openError(window.getShell(), "View version history", e.getLocalizedMessage());
                } else {
                    AdvancedExceptionDialog.showUnexpectedExceptionDialog(e, "Cant view version history");
                }
            }
        }

    }

    /**
     * Callback for finishing the ListVersionJob.
     *
     * @param job
     */
    private void onListVersionFinished(ListVersionsJob job) {

    }

}
