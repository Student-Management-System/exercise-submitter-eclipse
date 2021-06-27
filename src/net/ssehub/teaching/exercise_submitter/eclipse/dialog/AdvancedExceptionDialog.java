package net.ssehub.teaching.exercise_submitter.eclipse.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;

public class AdvancedExceptionDialog {

	private String message;
	private Throwable t;

	public AdvancedExceptionDialog(String message, Throwable t) {
		this.message = message;
		this.t = t;
	}

	public void open() {
		MultiStatus status = createMultiStatus(this.t);
		ErrorDialog.openError(new Shell(), "Error", this.message, status);
	}

	private static MultiStatus createMultiStatus(Throwable t) {

		List<Status> childStatuses = new ArrayList<>();
		StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();

		for (StackTraceElement stackTrace : stackTraces) {
			Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, stackTrace.toString());
			childStatuses.add(status);
		}

		MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, childStatuses.toArray(new Status[] {}),
				t.toString(), t);
		return ms;
	}
}
