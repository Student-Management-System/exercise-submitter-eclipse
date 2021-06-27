package net.ssehub.teaching.exercise_submitter.eclipse.log;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;

public class Errorlog {
	private static ILog ilog = Activator.getDefault().getLog();

	public static void add(String exception) {
		Errorlog.ilog.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, exception));
	}
}
