package net.ssehub.teaching.exercise_submitter.eclipse.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;

/**
 * TODO: how is this used?
 * 
 * @author Lukas
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

    }

}
