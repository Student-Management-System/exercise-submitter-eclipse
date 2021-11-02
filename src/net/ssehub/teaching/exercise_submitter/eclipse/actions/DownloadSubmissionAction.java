package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.background.ReplayerJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.log.EclipseLog;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;

/**
 * Lets the user download a submission and creates a local project with the
 * content.
 *
 * @author Lukas
 */
public class DownloadSubmissionAction extends AbstractHandler {
    
    //TODO: remove selected dir 
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        IWorkbenchWindow window = null;
        
        if (event.getApplicationContext() instanceof IEvaluationContext) {
            IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();

            window = (IWorkbenchWindow) context.getVariable(ISources.ACTIVE_WORKBENCH_WINDOW_NAME);
           
        }

        ExerciseSubmitterManager manager = Activator.getDefault().getManager();
        Optional<Assignment> selectedAssigment = this.createAssignmentDialog(window, manager);
        
        if (selectedAssigment.isPresent()) {
            
            Replayer replayer = null;
            try {
                replayer = manager.getReplayer(selectedAssigment.get());
            } catch (IllegalArgumentException | ApiException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
            EclipseLog.info("Starting download from Assignment: " + selectedAssigment.get());
    
            ReplayerJob job = new ReplayerJob(window.getShell(), replayer, selectedAssigment.get(), 
                    this::onReplayFinished);
            job.setUser(true);
            job.schedule();
            
        } else {
            MessageDialog.openInformation(window.getShell(), "Exercise Submitter", "No Assignment selected");
        }
        return null;
      
    }
    
    /**
     * Creates the assignmentdialog.
     * @param window , current window
     * @param manager , authented submittermanager
     * @return an Optional Assignment, the selected assignment
     */
    private Optional<Assignment> createAssignmentDialog(IWorkbenchWindow window, ExerciseSubmitterManager manager) {
        Optional<Assignment> selected = Optional.empty();

        try {
            AssignmentDialog assDialog = new AssignmentDialog(window.getShell(), manager.getAllSubmittableAssignments(),
                    AssignmentDialog.Sorted.NONE);

            int dialogResult;

            do {

                dialogResult = assDialog.open();
                // only use selected Assignment if user press ok
                if (dialogResult == Window.OK) {
                    selected = assDialog.getSelectedAssignment();
                }

                // user press ok without selecting anything. -> Retry
            } while (dialogResult == Window.OK && selected.isEmpty());

        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return selected;
    }
    
    /**
     * Called when replay is finished.
     * @param job
     */
    private void onReplayFinished(ReplayerJob job) {
        System.out.println("Replay success");

    }
    
}
