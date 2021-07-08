package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Helper methods for events.
 * 
 * @author Adam
 */
class EventHelper {

    /**
     * No instances.
     */
    private EventHelper() {
    }
    
    /**
     * Gets the current active shell for a given event.
     * 
     * @param event The event.
     * 
     * @return The current shell.
     */
    public static Shell getShell(ExecutionEvent event) {
        IWorkbenchWindow window = null;

        if (event.getApplicationContext() instanceof IEvaluationContext) {
            IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();

            window = (IWorkbenchWindow) context.getVariable(ISources.ACTIVE_WORKBENCH_WINDOW_NAME);
        }
        
        Shell shell;
        if (window != null) {
            shell = window.getShell();
        } else {
            shell = new Shell();
        }
        
        return shell;
    }
    
}
