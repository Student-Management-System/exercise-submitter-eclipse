package net.ssehub.teaching.exercise_submitter.eclipse.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import net.ssehub.teaching.exercise_submitter.eclipse.Activator;



public class PreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	 private StringFieldEditor password;

	public PreferencePage() {
		super(GRID);
		//setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Exercise Submitter");
	}
	
	
	public void createFieldEditors() {
		addField(
			new StringFieldEditor(PreferenceConstants.USERNAME, "Username: ", getFieldEditorParent()));
		
		this.password = new StringFieldEditor(PreferenceConstants.PASSWORD, "Password: ", getFieldEditorParent());
		this.password.getTextControl(getFieldEditorParent()).setEchoChar('*');
		addField(password);
	}
	protected void performApply() {
		//was passieren soll beim apply drücken
		
	}
	
	public void init(IWorkbench workbench) {
	}
	
}