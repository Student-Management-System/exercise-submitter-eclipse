package net.ssehub.teaching.exercise_submitter.eclipse.dialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.eclipse.actions.SubmitAction;
import net.ssehub.teaching.exercise_submitter.eclipse.background.CheckSubmissionJob.CheckResult;
import net.ssehub.teaching.exercise_submitter.eclipse.background.ListVersionsJob;
import net.ssehub.teaching.exercise_submitter.eclipse.background.ReplayJob;
import net.ssehub.teaching.exercise_submitter.eclipse.background.SubmissionJob;
import net.ssehub.teaching.exercise_submitter.eclipse.utils.TimeUtils;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment.State;

/**
 * This creates the CheckSubmissionDialog.
 *
 * @author lukas
 * @author Adam
 */
public class CheckSubmissionDialog extends Dialog {

    private ExerciseSubmitterManager manager;
    
    private CheckResult checkresult;

    /**
     * Creates an instance of CheckSubmissionDialog.
     *
     * @param parentShell the parent shell
     * @param manager The manager to contact the student management system with.
     * @param result The result of comparing the contents.
     */
    public CheckSubmissionDialog(Shell parentShell, ExerciseSubmitterManager manager, CheckResult result) {
        super(parentShell);
        this.manager = manager;
        this.checkresult = result;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, CANCEL, "Close", true);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;

        container.setLayout(gridLayout);

        new Label(container, SWT.NULL).setText("Check result: ");

        Label resultLabel = new Label(container, SWT.RIGHT);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        resultLabel.setLayoutData(gridData);
        String resultString = this.checkresult.isSameContent() ? "The content is the same" : "The content differs";
        resultLabel.setText(resultString);

        new Label(container, SWT.NULL).setText("Latest timestamp: ");
        Label currentVersionLabel = new Label(container, SWT.RIGHT);
        currentVersionLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        String versionString = TimeUtils.instantToLocalString(this.checkresult.getVersion().getTimestamp());
        currentVersionLabel.setText(versionString);

        new Label(container, SWT.NULL).setText("List all versions: ");

        Button listVersionsButton = new Button(container, SWT.PUSH);
        listVersionsButton.setText("List Versions");
        listVersionsButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        listVersionsButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event)  {
                CheckSubmissionDialog.this.createListVersionList();
                CheckSubmissionDialog.this.close();
            }
        });

        if (!this.checkresult.isSameContent()) {

            if (this.checkresult.getAssignment().getState() == State.SUBMISSION) {
                new Label(container, SWT.NULL).setText("Submit local project: ");
                
                Button submitButton = new Button(container, SWT.PUSH);
                submitButton.setText("Submit");
                submitButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
                submitButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        CheckSubmissionDialog.this.createSubmissionJob();
                        CheckSubmissionDialog.this.close();
                    }
                    
                });
            }
            
            new Label(container, SWT.NULL).setText("Download latest version: ");

            Button downloadButton = new Button(container, SWT.PUSH);
            downloadButton.setText("Download");
            downloadButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
            downloadButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    CheckSubmissionDialog.this.createReplayerJob();
                    CheckSubmissionDialog.this.close();
                }
            });
        }
        return container;
    }

    /**
     * Create a SubmissionJob.
     */
    private void createSubmissionJob() {
        Shell shell = getParentShell();
        IProject project = this.checkresult.getProject();
        Assignment assignment = this.checkresult.getAssignment();
        
        SubmissionJob job = new SubmissionJob(shell, this.manager, assignment, project, 
                (submissionResult) -> {
                    SubmitAction.createSubmissionFinishedDialog(getParentShell(), project, assignment,
                            submissionResult);
                });
        job.schedule();
    }

    /**
     * Creates an ReplayJob.
     */
    private void createReplayerJob() {
        ReplayJob job = new ReplayJob(getParentShell(), this.manager, this.checkresult.getAssignment(),
                project -> MessageDialog.openInformation(getParentShell(), "Submission Download",
                        "Submission has been stored in project " + project.getName()));
        job.schedule();
    }
    
    /**
     * Creates an ListVerionJob.
     */
    private void createListVersionList() {
        ListVersionsJob job;
        job = new ListVersionsJob(getParentShell(), this.manager, this.checkresult.getAssignment(),
                ListVersionsJob.displayVersionsCallback(getParentShell(), this.checkresult.getAssignment().getName()));
        job.schedule();
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Check Submission result");
    }

    @Override
    protected Point getInitialSize() {
        return new Point(325, 225);
    }

}
