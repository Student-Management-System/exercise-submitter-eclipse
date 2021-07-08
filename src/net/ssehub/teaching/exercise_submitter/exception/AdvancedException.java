package net.ssehub.teaching.exercise_submitter.exception;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AdvancedExceptionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.log.Errorlog;

public class AdvancedException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 3393868964229934351L;

    public AdvancedException(String message) {
        super(message);
        this.log();
    }

    public void show() {
        AdvancedExceptionDialog ae = new AdvancedExceptionDialog(this.getMessage(), this);
        ae.open();
    }

    private void log() {
        Errorlog.add(this.getMessage());
    }
}
