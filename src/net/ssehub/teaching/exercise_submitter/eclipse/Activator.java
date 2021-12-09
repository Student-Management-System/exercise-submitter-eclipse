package net.ssehub.teaching.exercise_submitter.eclipse;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
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
    
    private Optional<ExerciseSubmitterManager> manager = Optional.empty();
    
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
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
        this.manager = Optional.empty();
        
        String course = "";
        try {
            Properties prop = new Properties();
            prop.load(Activator.class.getResourceAsStream("config.properties"));
            course = prop.getProperty("course");
            
            String username = PreferencePage.SECURE_PREFERENCES.get(PreferencePage.KEY_USERNAME, "");
            String password = PreferencePage.SECURE_PREFERENCES.get(PreferencePage.KEY_PASSWORD, "");
            
            ExerciseSubmitterFactory factory = new ExerciseSubmitterFactory();
            factory
                    .withUsername(username)
                    .withPassword(password)
                    .withCourse(course)
                    .withAuthUrl(prop.getProperty("authurl"))
                    .withMgmtUrl(prop.getProperty("mgmturl"))
                    .withExerciseSubmitterServerUrl(prop.getProperty("exerciseSubmitterUrl"));
            this.manager = Optional.of(factory.build());
            
        } catch (StorageException ex) {
            ExceptionDialogs.showUnexpectedExceptionDialog(ex, "Failed to load login data from preferences");
        } catch (NetworkException e) {
            ExceptionDialogs.showNetworkExceptionDialog(e);
        } catch (UserNotInCourseException e) {
            ExceptionDialogs.showUserNotInCourseDialog(course);
        } catch (AuthenticationException e) {
            ExceptionDialogs.showLoginFailureDialog();
        } catch (ApiException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Generic API exception");
        } catch (IOException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Cant read config file");
        }
    }
    
    /**
     * Returns the {@link ExerciseSubmitterManager}. Manager is lazily initialized.
     * 
     * @return The {@link ExerciseSubmitterManager}, or {@link Optional#empty()} if it could not be created (e.g. due to
     *      login issues.
     */
    public synchronized Optional<ExerciseSubmitterManager> getManager() {
        if (manager.isEmpty()) {
            initManager();
        }
        return manager;
    }
    
    /**
     * Checks whether the {@link ExerciseSubmitterManager} is initialized.
     * 
     * @return Whether the manager is initialized.
     */
    public synchronized boolean isManagerInitialized() {
        return manager.isPresent();
    }
    
}
