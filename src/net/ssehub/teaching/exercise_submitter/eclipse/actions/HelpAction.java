package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Course;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.IApiConnection;

/**
 * Shows a help dialog to the user.
 * 
 * @author Lukas
 */
public class HelpAction extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        StringBuilder message = new StringBuilder();
        
        if (Activator.getDefault().isManagerInitialized()) {
            ExerciseSubmitterManager manager = Activator.getDefault().getManager().get();
            IApiConnection connection = manager.getStudentManagementConnection();
            
            message.append("Course: ").append(manager.getCourse().getName())
                    .append(" (").append(manager.getCourse().getId()).append(')');
            
            message.append("\nUsername: ").append(connection.getUsername());
            
            message.append("\nRole: ").append(getRoleName(connection, manager.getCourse()));
            
        } else {
            message.append("Not yet logged in.");
        }
        
        Version version = FrameworkUtil.getBundle(getClass()).getVersion();
        message.append("\n\nPlug-In Version: ").append(version.toString());
        
        // TODO: more content
        MessageDialog.openInformation(EventHelper.getShell(event), "Exercise Submitter", message.toString());
        return null;
    }
    
    /**
     * Returns a string representation of the role of the logged-in user in the given course.
     * 
     * @param connection The connection with a logged-in user.
     * @param course The course to get the the role for.
     * 
     * @return The role of the user.
     */
    private static String getRoleName(IApiConnection connection, Course course) {
        String role;
        boolean tutor;
        try {
            tutor = connection.hasTutorRights(course);
            if (tutor) {
                role = "Tutor";
            } else {
                role = "Student";
            }
        } catch (ApiException e) {
            role = "(error)";
        }
        return role;
    }

}
