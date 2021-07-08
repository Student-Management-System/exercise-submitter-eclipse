package net.ssehub.teaching.exercise_submitter.eclipse.log;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;

/**
 * Utility methods for error logging.
 * 
 * @author Lukas
 */
public class Errorlog {
    
    private static ILog ilog = Activator.getDefault().getLog();

    /**
     * Logs an exception text.
     * 
     * @param exception The exception text.
     */
    public static void add(String exception) {
        Errorlog.ilog.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, exception));
    }

}
