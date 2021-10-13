package net.ssehub.teaching.exercise_submitter.eclipse.preferences;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment.State;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;

/**
 * This class handles the project connection with an assignment.
 *
 * @author lukas
 *
 */
public class ProjectManager {

    private static Preferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);

    /**
     * This method instantiates an new ProjectManager.
     */
    public ProjectManager() {

    }

    /**
     * This method sets a connection between a project and an assignment.
     *
     * @param project
     * @param assignment
     * @throws BackingStoreException
     */
    public void setConnection(IProject project, Assignment assignment) throws BackingStoreException {
        preferences.put(project.getLocation().toString(), assignment.getManagementId());
        preferences.put(assignment.getManagementId(), assignment.getName());
        preferences.flush();
    }

    /**
     * This method gets the connection from a project to an assignment if available.
     *
     * @param project
     * @return Assignment
     * @throws ProjectManagerException
     */
    public Assignment getConnection(IProject project) throws ProjectManagerException {
        String assignmentid = preferences.get(project.getLocation().toString(), null);
        Assignment assignment = null;
        if (assignmentid != null) {
            String assignmentname = preferences.get(assignmentid, null);

            if (assignmentname == null) {
                throw new ProjectManagerException(ProjectManagerException.NOTCONNECTED);
            }

            assignment = new Assignment(assignmentid, assignmentname, State.SUBMISSION, true);

            try {
                if (!this.checkIfConnectedAssignmentisSubmittable(assignment)) {
                    throw new ProjectManagerException(ProjectManagerException.NOTAVAILABLE);

                }
            } catch (IllegalArgumentException | ApiException e) {
                throw new ProjectManagerException(ProjectManagerException.LISTVERSIONFAILURE, e);
            }

        } else {
            throw new ProjectManagerException(ProjectManagerException.NOTCONNECTED);
        }
        return assignment;

    }

    /**
     * Checks if a specific assignment is available to submit.
     *
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

        if (assignments.size() != 0 && assignments.stream()
                .filter(listelement -> listelement.getManagementId().equals(assignment.getManagementId()))
                .count() > 0) {
            result = true;
        }

        return result;
    }

}
