package net.ssehub.teaching.exercise_submitter.exception;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class UserException extends Exception {

    public static final String EXCEPTION_LIST_NOTSELECTED = "You need to select something";
    
    private static final long serialVersionUID = -4648322879145949773L;

    public UserException(String message) {
        super(message);
    }

    public void show() {
        MessageDialog.openError(new Shell(), "Error", this.getMessage());
    }

}
