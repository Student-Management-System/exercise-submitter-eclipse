package net.ssehub.teaching.exercise_submitter.eclipse;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.preferences.PreferencePage;
import net.ssehub.teaching.exercise_submitter.lib.Manager;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "exercise-submitter-eclipse";

    private static Activator plugin;

    private Manager manager;

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
     * Initializes the {@link Manager} with the username and password from the preference store.
     * <p>
     * May be called multiple times, if the username or password in the preference store change.
     */
    public synchronized void initManager() {
        ISecurePreferences securePreferences = SecurePreferencesFactory.getDefault();
        try {
            String username = securePreferences.get(PreferencePage.KEY_USERNAME, ""); // 1.05 1.17
            String password = securePreferences.get(PreferencePage.KEY_PASSWORD, "");
            manager = new Manager(username, password.toCharArray());
        }
            catch( StorageException ex) {
                AdvancedExceptionDialog ae = new AdvancedExceptionDialog("Unexpected Error occured", ex);
                ae.open(Display.getCurrent().getActiveShell());
            }
    }
    
    /**
     * Returns the {@link Manager}. Manager is lazily initialized.
     * 
     * @return The {@link Manager}.
     */
    public synchronized Manager getManager() {
        if (manager == null) {
            initManager();
        }
        return manager;
    }

}
