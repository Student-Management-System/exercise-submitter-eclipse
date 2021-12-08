package net.ssehub.teaching.exercise_submitter.eclipse.dialog;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;

/**
 * Utility class for showing error dialogs for exceptions.
 * 
 * @author Adam
 * @author Lukas
 */
public class ExceptionDialogs  {

    /**
     * No instances.
     */
    private ExceptionDialogs() {
    }
    
    /**
     * Opens an error dialog that shows the given message and the stacktrace of the given exception as advanced info.
     * 
     * @param title The title of the dialog.
     * @param message1 The first (top) message of the dialog.
     * @param message2 The second (bottom) message of the dialog.
     * @param exception The exception to get the stacktrace from.
     */
    private static void showErrorDialogWithStacktrace(String title, String message1, String message2,
            Throwable exception) {
        StringWriter stacktrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stacktrace));
        
        IStatus inner = new Status(IStatus.ERROR, Activator.PLUGIN_ID, stacktrace.toString());
        MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, new IStatus[] {inner}, message2, null);
        // TODO: the stack trace cannot be easily copied in this dialog... maybe use a different method?
        
        ErrorDialog.openError(Display.getDefault().getActiveShell(), title, message1, status);
    }
    
    /**
     * Creates an error dialog for an unexpected exception.
     * 
     * @param exc The unexpected exception.
     * @param reason A short description of the reason that caused the exception. Alternatively, a description of the
     *      operation that failed. E.g.: <code>"Failed to load preferences"</code>
     */
    public static void showUnexpectedExceptionDialog(Throwable exc, String reason) {
        StringWriter stacktrace = new StringWriter();
        exc.printStackTrace(new PrintWriter(stacktrace));
        
        showErrorDialogWithStacktrace("Unexpected Error", "An unexpected error occurred.", reason, exc);
    }

    /**
     * Shows an error dialog that informs the user that logging into the student management system failed.
     */
    public static void showLoginFailureDialog() {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Login Failed", "Failed to log into the student"
                + " management system.\n\nPlease make sure that the login data in the preference page is correct.");
    }
    
    /**
     * Shows an error dialog that informs the user that a {@link NetworkException} has occurred.
     * 
     * @param exception The {@link NetworkException} that occurred.
     */
    public static void showNetworkExceptionDialog(NetworkException exception) {
        showErrorDialogWithStacktrace("Network Problem", "A network exception occurred.",
                exception.getMessage(), exception);
    }
    
    /**
     * Shows an error dialog that informs the user that they are not enrolled in the given course.
     * 
     * @param courseId The course that the user is not enrolled in.
     */
    public static void showUserNotInCourseDialog(String courseId) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Not In Course",
                "You are not enrolled in the course " + courseId
                + ".\n\nPlease log into the student management system and enroll yourself in this course.");
    }
    
    /**
     * Shows an error dialog that informs the user that they are not in a group for the given assignment.
     * 
     * @param assignment The name of the assignment.
     */
    public static void showUserNotInGroupDialog(String assignment) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Not In Group",
                "You are not a memeber of a group in assignment " + assignment
                + ".\n\nGroups for assignments are created when the assignment starts. Please contact a tutor if you"
                + " want to be added to a group after an assignment has already started.");
    }
    
}
