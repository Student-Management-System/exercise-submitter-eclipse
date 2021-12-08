package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.VersionListDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.VersionSelectionDialog;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.replay.ReplayException;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer.Version;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.GroupNotFoundException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;

/**
 * A job that lists the versions of a given assignment.
 * 
 * @author Adam
 * @author lukas
 */
public class ListVersionsJob extends AbstractJob<List<Version>> {

    private ExerciseSubmitterManager manager;
    
    private Assignment assignment;
    
    /**
     * Creates a new job.
     * 
     * @param shell The parent shell.
     * @param manager The manager to contact the student management system.
     * @param assignment The assignment to get the version list for. 
     * @param callback The callback that is called when a version list is available.
     */
    public ListVersionsJob(Shell shell, ExerciseSubmitterManager manager, Assignment assignment,
            Consumer<List<Version>> callback) {
        super("List Versions", shell, callback);
        this.manager = manager;
        this.assignment = assignment;
    }

    @Override
    protected Optional<List<Version>> run() {
        Optional<List<Version>> result = Optional.empty();
        
        try (Replayer replayer = this.manager.getReplayer(this.assignment)) {
            
            result = Optional.of(replayer.getVersions());
            
        } catch (NetworkException e) {
            ExceptionDialogs.showNetworkExceptionDialog(e);
        } catch (UserNotInCourseException e) {
            ExceptionDialogs.showUserNotInCourseDialog(manager.getCourse().getId());
        } catch (GroupNotFoundException e) {
            ExceptionDialogs.showUserNotInGroupDialog(assignment.getName());
        } catch (AuthenticationException e) {
            ExceptionDialogs.showLoginFailureDialog();
        } catch (ApiException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Generic API exception");
        } catch (IOException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Failed to store replay");
        } catch (ReplayException e) {
            ExceptionDialogs.showUnexpectedExceptionDialog(e, "Failed to replay");
        }
        
        return result;
    }
    
    /**
     * Creates a callback for a {@link ListVersionsJob} that shows a dialog that lists the versions.
     * 
     * @param shell The shell to open the dialog for.
     * @param assignmentName The name of the assignment that the versions are displayed for.
     * 
     * @return A callback.
     */
    public static Consumer<List<Version>> displayVersionsCallback(Shell shell, String assignmentName) {
        return (versions) -> {
            VersionListDialog dialog = new VersionListDialog(shell, assignmentName, versions);
            dialog.open();
        };
    }
    
    /**
     * Creates a callback for a {@link ListVersionsJob} that shows a dialog that lets the user select an assignment.
     * 
     * @param shell The shell to open the dialog for.
     * @param assignmentName The name of the assignment that the versions are displayed for.
     * @param selectionCallback A callback that will be called with the user-selected version.
     * 
     * @return A callback.
     */
    public static Consumer<List<Version>> selectVersionCallback(Shell shell, String assignmentName,
            Consumer<Version> selectionCallback) {
        
        return (versions) -> {
            VersionSelectionDialog dialog = new VersionListDialog(shell, assignmentName, versions);
            Optional<Version> selected = dialog.openAndGetSelectedVersion();
            selected.ifPresent(selectionCallback);
        };
    }

}
