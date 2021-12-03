package net.ssehub.teaching.exercise_submitter.eclipse.preferences;

import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.widgets.Display;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;

/**
 * This class handles the project connection with an assignment.
 *
 * @author Adam
 * @author lukas
 *
 */
public class ProjectManager {

    /**
     * Global singleton instance.
     */
    public static final ProjectManager INSTANCE = new ProjectManager();
    
    private static final QualifiedName CONNECTED_ASSIGNMENT_NAME = new QualifiedName(Activator.PLUGIN_ID, "assignment");
    
    /**
     * No other instances but the singleton instance.
     */
    private ProjectManager() {
    }

    /**
     * This method sets a connection between a project and an assignment.
     *
     * @param project The project to add the assignment connection for.
     * @param assignment The assignment to associate with the given project.
     */
    public void setConnection(IProject project, Assignment assignment) {
        try {
            project.setPersistentProperty(CONNECTED_ASSIGNMENT_NAME, assignment.getName());
        } catch (CoreException e) {
            Display.getDefault().syncExec(() -> {
                ExceptionDialogs.showUnexpectedExceptionDialog(e, "Failed to store assignment name");
            });
        }
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
        Optional<String> assignmentName = Optional.empty();
        if (project.isOpen()) {
            try {
                assignmentName = Optional.ofNullable(project.getPersistentProperty(CONNECTED_ASSIGNMENT_NAME));
            } catch (CoreException e) {
                Display.getDefault().syncExec(() -> {
                    ExceptionDialogs.showUnexpectedExceptionDialog(e, "Failed to load assignment name");
                });
            }
        }
        return assignmentName;
    }

    /**
     * Returns the assignment if its available on the server.
     *
     * @param assignmentName The name of the assignment.
     * @param manager The {@link ExerciseSubmitterManager} to get {@link Assignment}s from.
     * 
     * @return The found assignment, or {@link Optional#empty()} if no assignment with this name exists.
     * 
     * @throws ApiException If retrieving all assignment fails.
     */
    private Optional<Assignment> getAssignmentByName(String assignmentName, ExerciseSubmitterManager manager)
            throws NetworkException, AuthenticationException, UserNotInCourseException, ApiException {
        
        Optional<Assignment> result = manager.getAllAssignments().stream()
                .filter(assignment -> assignment.getName().equals(assignmentName))
                .findFirst();
        
        return result;
    }

    /**
     * This method gets the connection from a project to an assignment if available.
     *
     * @param project
     * @param manager
     * @return The connected assignment, or {@link Optional#empty()} if the given project has no connected assignment.
     */
    public Optional<Assignment> getConnection(IProject project, ExerciseSubmitterManager manager)
            throws NetworkException, AuthenticationException, UserNotInCourseException, ApiException {
        
        Optional<Assignment> assignment = Optional.empty();
        
        Optional<String> name = getStoredAssignmentName(project);
        if (name.isPresent()) {
            assignment = getAssignmentByName(name.get(), manager);
        }
        
        return assignment;
    }

}
