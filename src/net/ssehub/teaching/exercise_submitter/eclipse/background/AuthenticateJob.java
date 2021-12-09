package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;

/**
 * A job that retrieves an authenticated {@link ExerciseSubmitterManager}.
 * 
 * @author Adam
 */
public class AuthenticateJob extends AbstractJob<ExerciseSubmitterManager> {

    /**
     * Creates a new instance.
     * 
     * @param shell The parent shell that this job runs for.
     * @param callback The callback to call with the {@link ExerciseSubmitterManager}.
     */
    public AuthenticateJob(Shell shell, Consumer<ExerciseSubmitterManager> callback) {
        super("Logging into Student Management System", shell, callback);
    }

    @Override
    protected Optional<ExerciseSubmitterManager> run() {
        return Activator.getDefault().getManager();
    }

}
