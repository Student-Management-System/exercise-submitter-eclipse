package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

/**
 * An abstract superclass for jobs that run asynchronously.
 * 
 * @param <T> The result type of this job.
 * 
 * @author Adam
 */
public abstract class AbstractJob<T> extends Job {
    
    private static final ILock LOCK = Job.getJobManager().newLock();

    protected Shell shell;
    
    protected IProgressMonitor monitor;
        
    private Consumer<T> callback;
    
    /**
     * Creates a new job.
     * <p>
     * Use {@link #schedule()} to start the job.
     * 
     * @param name The name of this job.
     * @param shell The shell that this job belongs to.
     * @param callback A callback that will be called on the GUI thread with the result created by this job.
     */
    public AbstractJob(String name, Shell shell, Consumer<T> callback) {
        super(name);
        this.shell = shell;
        this.callback = callback;
        
        setUser(true);
    }

    @Override
    protected final IStatus run(IProgressMonitor monitor) {
        try {
            LOCK.acquire();
            
            this.monitor = monitor;
            run().ifPresent(result -> {
                shell.getDisplay().asyncExec(() -> {
                    callback.accept(result);
                });
            });
            
        } finally {
            LOCK.release();
        }
        
        return Status.OK_STATUS;
    }
    
    /**
     * Executes this job.
     * 
     * @return The return type of this job, or {@link Optional#empty()} if an error occurred and no normal result can
     *      be produced.
     */
    protected abstract Optional<T> run();

}
