package net.ssehub.teaching.exercise_submitter.eclipse.labels;

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
 * This class handles the association of projects to assignments.
 *
 * @author Adam
 * @author lukas
 */
public class ProjectAssignmentMapper {

    /**
     * Global singleton instance.
     */
    public static final ProjectAssignmentMapper INSTANCE = new ProjectAssignmentMapper();
    
    private static final QualifiedName PROPERTY_NAME = new QualifiedName(Activator.PLUGIN_ID, "assignment");
    
    /**
     * No other instances but the singleton instance.
     */
    private ProjectAssignmentMapper() {
    }

    /**
     * Associates the given project with the given assignment.
     *
     * @param project The project to add the assignment association for.
     * @param assignment The assignment to associate with the given project.
     */
    public void setAssociation(IProject project, Assignment assignment) {
        try {
            project.setPersistentProperty(PROPERTY_NAME, assignment.getName());
            AssignmentLabelDecorator.update(project);
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
    public Optional<String> getAssociatedAssignmentName(IProject project) {
        Optional<String> assignmentName = Optional.empty();
        if (project.isOpen()) {
            try {
                assignmentName = Optional.ofNullable(project.getPersistentProperty(PROPERTY_NAME));
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
     * Retrieves the assignment associated with the given project.
     *
     * @param project The project to get the associated assignment for.
     * @param manager The manager to retrieve assignments with.
     * @return The associated assignment, or {@link Optional#empty()} if the given project has no associated assignment.
     */
    public Optional<Assignment> getAssociatedAssignment(IProject project, ExerciseSubmitterManager manager)
            throws NetworkException, AuthenticationException, UserNotInCourseException, ApiException {
        
        Optional<Assignment> assignment = Optional.empty();
        
        Optional<String> name = getAssociatedAssignmentName(project);
        if (name.isPresent()) {
            assignment = getAssignmentByName(name.get(), manager);
        }
        
        return assignment;
    }

}
