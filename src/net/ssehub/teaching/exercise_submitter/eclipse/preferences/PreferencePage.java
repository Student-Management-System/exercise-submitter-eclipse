package net.ssehub.teaching.exercise_submitter.eclipse.preferences;

import java.io.IOException;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;

/**
 * Handler for the preference page.
 * 
 * @author Lukas
 * @author Adam
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static final String KEY_USERNAME = "net.ssehub.teaching.exercise_submitter.eclipse.config.username";

    public static final String KEY_PASSWORD = "net.ssehub.teaching.exercise_submitter.eclipse.config.password";
    
    public static final ISecurePreferences SECURE_PREFERENCES = SecurePreferencesFactory.getDefault();
    
    private StringFieldEditor username;
    
    private StringFieldEditor password;

    /**
     * Creates a new instance.
     */
    public PreferencePage() {
        super(GRID);
        this.setDescription("Exercise Submitter");
    }

    @Override
    public void createFieldEditors() {
        this.username = new StringFieldEditor(KEY_USERNAME, "Username:", this.getFieldEditorParent());
        this.addField(username);
       
        this.password = new StringFieldEditor(KEY_PASSWORD, "Password:", this.getFieldEditorParent());
        this.password.getTextControl(this.getFieldEditorParent()).setEchoChar('*');
        this.addField(this.password);
        
        try {
            this.username.setStringValue(SECURE_PREFERENCES.get(KEY_USERNAME, ""));
            this.password.setStringValue(SECURE_PREFERENCES.get(KEY_PASSWORD, ""));
            
        } catch (StorageException ex) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(ex, "Failed to load preferences");
        }
    }

    @Override
    public boolean performOk() {
        try {
            SECURE_PREFERENCES.put(KEY_USERNAME, this.username.getStringValue(), false);
            SECURE_PREFERENCES.put(KEY_PASSWORD, this.password.getStringValue(), true);
            SECURE_PREFERENCES.flush();
            
            Activator.getDefault().initManager();
            
        } catch (StorageException | IOException ex) {
            AdvancedExceptionDialog.showUnexpectedExceptionDialog(ex, "Failed to store preferences");
        }
        
        return true;
    }
    
    @Override
    protected void performDefaults() {
        this.username.setStringValue("");
        this.password.setStringValue("");
    }
    
    @Override
    public void init(IWorkbench workbench) {
    }

}
