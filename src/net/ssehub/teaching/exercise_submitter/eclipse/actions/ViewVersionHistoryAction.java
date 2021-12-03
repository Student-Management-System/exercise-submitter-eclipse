package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.background.ListVersionsJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.eclipse.preferences.ProjectManagerException;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;

/**
 * Action for display the version history for the selected Project.
 *
 * @author lukas
 *
 */
public class ViewVersionHistoryAction extends AbstractSingleProjectActionUsingManager {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window, ExerciseSubmitterManager manager) {

        Assignment assignment;
        try {
            assignment = Activator.getDefault().getProjectManager().getConnection(project, manager);
            ListVersionsJob job;
            
            EclipseLog.info("Version log of assignment " + assignment.getName() + "downloading");
            
            
            job = new ListVersionsJob(window.getShell(), manager.getReplayer(assignment),
                    assignment, this::onListVersionFinished);
            job.setUser(true);
            job.schedule();
        } catch (ProjectManagerException | IllegalArgumentException | ApiException e) {
            if (e instanceof ProjectManagerException) {
                if (e.getMessage().equals(ProjectManagerException.NOTAVAILABLE)
                        || e.getMessage().equals(ProjectManagerException.NOTCONNECTED)) {
                    MessageDialog.openError(window.getShell(), "View version history", e.getLocalizedMessage());
                } else {
                    ExceptionDialogs.showUnexpectedExceptionDialog(e, "Cant view version history");
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
