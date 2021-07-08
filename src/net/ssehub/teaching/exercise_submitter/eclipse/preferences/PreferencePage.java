package net.ssehub.teaching.exercise_submitter.eclipse.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private StringFieldEditor password;

    public PreferencePage() {
        super(GRID);
        // setPreferenceStore(Activator.getDefault().getPreferenceStore());
        this.setDescription("Exercise Submitter");
    }

    @Override
    public void createFieldEditors() {
        this.addField(new StringFieldEditor(PreferenceConstants.USERNAME, "Username: ", this.getFieldEditorParent()));

        this.password = new StringFieldEditor(PreferenceConstants.PASSWORD, "Password: ", this.getFieldEditorParent());
        this.password.getTextControl(this.getFieldEditorParent()).setEchoChar('*');
        this.addField(this.password);
    }

    @Override
    protected void performApply() {
        // was passieren soll beim apply drücken

    }

    @Override
    public void init(IWorkbench workbench) {
    }

}