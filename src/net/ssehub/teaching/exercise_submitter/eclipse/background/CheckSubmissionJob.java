package net.ssehub.teaching.exercise_submitter.eclipse.background;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.background.CheckSubmissionJob.CheckResult;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.CheckSubmissionDialog;
import net.ssehub.teaching.exercise_submitter.eclipse.dialog.ExceptionDialogs;
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
 * A background job that checks if the given project content equal the latest submission to the given assignment.
 * 
 * @author lukas
 * @author Adam
 */
public class CheckSubmissionJob extends AbstractJob<CheckResult> {

    private ExerciseSubmitterManager manager;
    
    private Assignment assignment;
    
    private IProject project;
    
    /**
     * Creates a new job.
     * 
     * @param shell The shell that this job belongs to.
     * @param manager The manager to contact the student management system with.
     * @param assignment The assignment to check.
     * @param project The project to check.
     */
    public CheckSubmissionJob(Shell shell, ExerciseSubmitterManager manager, Assignment assignment, IProject project) {
        super("Check SubmissionJob", shell, result -> {
            CheckSubmissionDialog dialog = new CheckSubmissionDialog(shell, manager, result);
            dialog.open();
        });
        this.manager  = manager;
        this.assignment = assignment;
        this.project = project;
    }
    
    /**
     * Represents the result of checking whether a project has the same content as the latest submission.
     */
    public static class CheckResult {
        
        private boolean sameContent;
        
        private Assignment assignment;
        
        private IProject project;
        
        private Version version;
        
        /**
         * Creates a check result.
         * 
         * @param sameContent Whether the latest submission and the assignment have the same content.
         * @param assignment The assignment that was checked.
         * @param project The project that was checked.
         * @param version The version of the assignment that was checked.
         */
        public CheckResult(boolean sameContent, Assignment assignment, IProject project, Version version) {
            this.sameContent = sameContent;
            this.assignment = assignment;
            this.project = project;
            this.version = version;
        }

        /**
         * Whether the content of the project and the latest submission are the same.
         * 
         * @return Whether the content is equal.
         */
        public boolean isSameContent() {
            return sameContent;
        }
        
        /**
         * Returns the assignment that the project was checked against.
         * 
         * @return The assignment.
         */
        public Assignment getAssignment() {
            return assignment;
        }
        
        /**
         * Returns the project that was checked.
         * 
         * @return The project.
         */
        public IProject getProject() {
            return project;
        }
        
        /**
         * Returns the version of the assignment that was checked.
         * 
         * @return The version.
         */
        public Version getVersion() {
            return version;
        }
        
    }

    @Override
    protected Optional<CheckResult> run() {
        
        Optional<CheckResult> result = Optional.empty();
        
        try (Replayer replayer = this.manager.getReplayer(this.assignment)) {
            
            List<Version> versions = replayer.getVersions();
            if (!versions.isEmpty()) {
                Version latest = versions.get(0);
                
                boolean sameContent = replayer.isSameContent(this.project.getLocation().toFile(), latest);
                
                result = Optional.of(new CheckResult(sameContent, this.assignment, this.project, latest));
                
            } else {
                this.shell.getDisplay().asyncExec(() -> {
                    MessageDialog.openWarning(this.shell, "Check Submission", "No submission uploaded to "
                            + this.assignment.getName());
                });
            }
            
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

}
