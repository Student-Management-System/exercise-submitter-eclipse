package net.ssehub.teaching.exercise_submitter.eclipse.preferences;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
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
        preferences.put(project.getLocation().toString(), assignment.getName());
        preferences.flush();
    }

    /**
     * Retrieves the name of the {@link Assignment} that is associated with the
     * given project.
     * 
     * @param project The project to get the {@link Assignment} for.
     *
     * @return The name of the {@link Assignment}, or empty.
     */
    public Optional<String> getStoredAssignmentName(IProject project) {
        return Optional.ofNullable(preferences.get(project.getLocation().toString(), null));
    }

    /**
     * Returns the assignment if its available on the server.
     *
     * @param assignmentName The name of the assignment.
     * @return The found assignment, or {@link Optional#empty()} if no assignment with this name xists.
     */
    private Optional<Assignment> getAssignmentByName(String assignmentName) {
        Assignment assignment = null;
        try {
            List<Assignment> assignmentlist = Activator.getDefault().getManager().getAllAssignments().stream()
                    .filter(element -> element.getName().equals(assignmentName)).collect(Collectors.toList());
            if (assignmentlist.size() == 1) {
                assignment = assignmentlist.get(0);
            }
        } catch (ApiException e) {
            // TODO: maybe better catch or make a throw
        }
        return Optional.ofNullable(assignment);
    }

    /**
     * This method gets the connection from a project to an assignment if available.
     *
     * @param project
     * @return Assignment
     * @throws ProjectManagerException
     */
    public Assignment getConnection(IProject project) throws ProjectManagerException {
        String assignemntName = preferences.get(project.getLocation().toString(), null);
        Assignment assignment = null;
        if (assignemntName != null) {

            Optional<Assignment> downloadedAssignment = getAssignmentByName(assignemntName);

            if (downloadedAssignment.isEmpty()) {
                throw new ProjectManagerException(ProjectManagerException.NOTCONNECTED);
            }

            assignment = downloadedAssignment.get();

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
