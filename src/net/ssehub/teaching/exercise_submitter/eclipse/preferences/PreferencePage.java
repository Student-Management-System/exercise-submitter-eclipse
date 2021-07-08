package net.ssehub.teaching.exercise_submitter.eclipse.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;

/**
 * Handler for the preference page.
 * 
 * @author Lukas
 * @author Adam
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static final String KEY_USERNAME = "net.ssehub.teaching.exercise_submitter.eclipse.config.username";

    public static final String KEY_PASSWORD = "net.ssehub.teaching.exercise_submitter.eclipse.config.password";
    
    private StringFieldEditor username;
    
    private StringFieldEditor password;

    /**
     * Creates a new instance.
     */
    public PreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        this.setDescription("Exercise Submitter");
    }

    @Override
    public void createFieldEditors() {
        this.username = new StringFieldEditor(KEY_USERNAME, "Username:", this.getFieldEditorParent());
        this.addField(username);

        // TODO: store this in a secure storage
        this.password = new StringFieldEditor(KEY_PASSWORD, "Password:", this.getFieldEditorParent());
        this.password.getTextControl(this.getFieldEditorParent()).setEchoChar('*');
        this.addField(this.password);
    }

    @Override
    protected void performApply() {
        // seems to be necessary to manually call store, so that initManager() can get the updated values
        username.store();
        password.store();
        Activator.getDefault().initManager();
    }

    @Override
    public void init(IWorkbench workbench) {
    }

}
