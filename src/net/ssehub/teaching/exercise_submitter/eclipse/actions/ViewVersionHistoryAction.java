package net.ssehub.teaching.exercise_submitter.eclipse.actions;



import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.osgi.service.prefs.Preferences;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.background.ListVersionsJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment.State;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;


/**
 * Action for display the version history for the selected Project.
 * @author lukas
 *
 */
public class ViewVersionHistoryAction extends AbstractSingleProjectAction {

    @Override
    protected void execute(IProject project, IWorkbenchWindow window)  { 
        Preferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        String assignmentid = preferences.get(project.getLocation().toString(), null);
        String assignmentname = preferences.get(assignmentid, null);
        if (assignmentid != null && assignmentname != null) {
            Assignment assignment = new Assignment(assignmentid, assignmentname, State.SUBMISSION, true);
            ListVersionsJob job;
            try {
                job = new ListVersionsJob(window.getShell(),
                        Activator.getDefault().getManager().getReplayer(assignment), assignment,
                        this::onListVersionFinished);
                job.setUser(true);
                job.schedule();
            } catch (IllegalArgumentException | ApiException | IOException e) {
                AdvancedExceptionDialog.showUnexpectedExceptionDialog(e, "Cant list the version history");
            }
            
        } else {
            MessageDialog.openError(window.getShell(), "Version history", "Not connected to an assignment");
        }
    }
    /**Callback for finishing the ListVersionJob.
     * 
     * @param job
     */
    private void onListVersionFinished(ListVersionsJob job) {
        
    }

}
