package net.ssehub.teaching.exercise_submitter.eclipse.exception;

import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.log.Errorlog;

public class AdvancedException extends Exception {

    private static final long serialVersionUID = 3393868964229934351L;

    public AdvancedException(String message) {
        super(message);
        this.log();
    }

    public void show(Shell shell) {
        AdvancedExceptionDialog ae = new AdvancedExceptionDialog(this.getMessage(), this);
        ae.open(shell);
    }

    private void log() {
        Errorlog.add(this.getMessage());
    }
}
