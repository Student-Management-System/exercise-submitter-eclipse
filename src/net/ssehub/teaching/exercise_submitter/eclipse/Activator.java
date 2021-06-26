package net.ssehub.teaching.exercise_submitter.eclipse;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import net.ssehub.teaching.exercise_submitter.eclipse.eclipsemanager.EclipseManager;
import net.ssehub.teaching.exercise_submitter.lib.Manager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "exercise-submitter-eclipse";

    private static Activator plugin;
    
    private static EclipseManager EManager = new EclipseManager();
    
    public Activator() {
    }

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
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
    public static EclipseManager getEclipseManager() {
    	return EManager;
    }

}
