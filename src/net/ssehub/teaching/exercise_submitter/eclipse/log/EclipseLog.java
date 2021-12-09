package net.ssehub.teaching.exercise_submitter.eclipse.log;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;

/**
 * Utility methods for logging to the eclipse log.
 * 
 * @author Adam
 * @author Lukas
 */
public class EclipseLog {
    
    private static final ILog LOG = Activator.getDefault().getLog();

    /**
     * No instances.
     */
    private EclipseLog() {
    }
    
    /**
     * Logs an error message.
     * 
     * @param message The error text.
     */
    public static void error(String message) {
        LOG.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
    }
    
    /**
     * Logs a warning message.
     * 
     * @param message The warning text.
     */
    public static void warning(String message) {
        LOG.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, message));
    }

}
