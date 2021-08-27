package net.ssehub.teaching.exercise_submitter.eclipse;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.eclipse.preferences.PreferencePage;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterFactory;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "net.ssehub.teaching.exercise-submitter-eclipse";

    private static Activator plugin;

    private ExerciseSubmitterManager manager;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        EclipseLog.info("Plug-in started");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
        EclipseLog.info("Plug-in stopped");
    }

    /**
     * Returns the shared instance.
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
    
    /**
     * Initializes the {@link ExerciseSubmitterManager} with the username and password from the preference store.
     * <p>
     * May be called multiple times, if the username or password in the preference store change.
     */
    public synchronized void initManager() {
        try {
            String username = PreferencePage.SECURE_PREFERENCES.get(PreferencePage.KEY_USERNAME, "");
            String password = PreferencePage.SECURE_PREFERENCES.get(PreferencePage.KEY_PASSWORD, "");
            
            EclipseLog.info("Creating manager with username " + username);
            ExerciseSubmitterFactory factory = new ExerciseSubmitterFactory();
            factory.withAuthUrl("");
            factory.withMgmtUrl("");
            factory.withSvnUrl("");
            factory.withUsername(username);
            factory.withPassword(password);
            factory.withCourse("java-wise2021");
            manager = factory.build();
            // TODO: get course name and semester and urls from config
            
        } catch (StorageException ex) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(ex, "Failed to load login data from preferences");
        } catch (NetworkException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e, "Failed to connect to student management system");
            // TODO: more user-friendly dialog?
        } catch (UserNotInCourseException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e,
                    "User not enrolled in course or course does not exist");
            // TODO: more user-friendly dialog?
        } catch (AuthenticationException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e, "Failed to log into student management system");
            // TODO: more user-friendly dialog
        } catch (ApiException e) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(e, "Generic API exception");
        }
    }
    
    /**
     * Returns the {@link ExerciseSubmitterManager}. Manager is lazily initialized.
     * 
     * @return The {@link ExerciseSubmitterManager}.
     */
    public synchronized ExerciseSubmitterManager getManager() {
        if (manager == null) {
            initManager();
        }
        return manager;
    }

}
