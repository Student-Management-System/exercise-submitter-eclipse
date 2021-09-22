package net.ssehub.teaching.exercise_submitter.eclipse.actions;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.Activator;
import net.ssehub.teaching.exercise_submitter.eclipse.background.ReplayerJob;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.AssignmentDialog;
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
public class DownloadSubmissionAction extends AbstractSingleProjectAction {

    @Override
    public void execute(IProject project, IWorkbenchWindow window) {

        ExerciseSubmitterManager manager = Activator.getDefault().getManager();
        Optional<Assignment> selectedAssigment = this.createAssignmentDialog(window, manager);

        Replayer replayer = null;
        try {
            replayer = manager.getReplayer(selectedAssigment.get());
        } catch (IllegalArgumentException | ApiException | IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        ReplayerJob job = new ReplayerJob(window.getShell(), replayer, selectedAssigment.get(),
                this::onVersionListFinished, this::onReplayFinished);
        job.setUser(true);
        job.schedule();

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
    /**
     * Called when VersionList is downloaded.
     * @param job
     */
    private void onVersionListFinished(ReplayerJob job) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject newProject = root.getProject(job.getAssignment().getName() + "-"
                + job.getVersion().get().getTimestamp().format(DateTimeFormatter.BASIC_ISO_DATE));
        try {
            newProject.create(null);
            newProject.open(null);
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        job.setLocation(Optional.ofNullable(newProject.getLocation().toFile()));
    }
}
