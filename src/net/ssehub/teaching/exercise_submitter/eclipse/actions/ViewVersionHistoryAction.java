package net.ssehub.teaching.exercise_submitter.eclipse.actions;



import java.io.IOException;
import java.util.List;

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
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;


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
        if (assignmentid != null && assignmentname != null ) {
            Assignment assignment = new Assignment(assignmentid, assignmentname, State.SUBMISSION, true);
            try {
                if (checkIfConnectedAssignmentisSubmittable(assignment)) {
      
                    ListVersionsJob job;
                    
                    job = new ListVersionsJob(window.getShell(),
                                Activator.getDefault().getManager().getReplayer(assignment), assignment,
                                this::onListVersionFinished);
                    job.setUser(true);
                    job.schedule();
              
                } else {
                    MessageDialog.openError(window.getShell(), "Version history", "Connected assignment not available");
                }
            } catch (IllegalArgumentException | ApiException | IOException e) {
                AdvancedExceptionDialog.showUnexpectedExceptionDialog(e, "Cant list the version history");
            }
            
        } else {
            MessageDialog.openError(window.getShell(), "Version history", "Not connected to an assignment");
        }
    }
    /**
     * Checks if a specific assignment is available to submit.
     * @param assignment
     * @throws ApiException 
     * @throws AuthenticationException 
     * @throws NetworkException
     * @return boolean 
     */
    private boolean checkIfConnectedAssignmentisSubmittable(Assignment assignment) 
            throws NetworkException, AuthenticationException, ApiException {
            
        boolean result = false;
        
        List<Assignment> assignments = Activator.getDefault().getManager().getAllSubmittableAssignments();
     
        if (assignments.size() != 0 && assignments.stream().filter(listelement -> 
            listelement.getManagementId().equals(assignment.getManagementId())).count() > 0) {
            result = true;
        }
        
        return result;
    }
    /**Callback for finishing the ListVersionJob.
     * 
     * @param job
     */
    private void onListVersionFinished(ListVersionsJob job) {
        
    }

}
