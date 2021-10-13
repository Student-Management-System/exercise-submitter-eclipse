package net.ssehub.teaching.exercise_submitter.eclipse.preferences;

/**
 * This class handles the Exceptions for the ProjectManager. 
 * @author lukas
 *
 */
public class ProjectManagerException extends Exception {

    
    
    public static final String NOTAVAILABLE = "Connected assignment not available";
    public static final String NOTCONNECTED = "Not connected to an assignment";
    public static final String LISTVERSIONFAILURE = "Cant list the version history";
    
    private static final long serialVersionUID = 1235810634456177790L;
    
    /**
     * This method instantiates a new Projectmanager exception.
     * @param message
     */
    public ProjectManagerException(String message) {
        super(message);
    }
    /**
     * This method instantiates a new Projectmanager Exception with a throwable.
     * @param message
     * @param throwable
     */
    public ProjectManagerException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
