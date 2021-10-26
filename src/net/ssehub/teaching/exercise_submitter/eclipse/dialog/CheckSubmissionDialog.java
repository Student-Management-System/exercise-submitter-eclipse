package net.ssehub.teaching.exercise_submitter.eclipse.dialog;

import java.time.format.DateTimeFormatter;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
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
import net.ssehub.teaching.exercise_submitter.eclipse.background.ReplayerJob;
import net.ssehub.teaching.exercise_submitter.eclipse.background.SubmissionJob;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer.Version;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;

/**
 * This creates the CheckSubmissionDialog.
 *
 * @author lukas
 *
 */
public class CheckSubmissionDialog extends Dialog {

    private java.util.List<Version> versionlist;
    private ExerciseSubmitterManager manager;
    private CheckResult checkresult;
    private IProject project;

    /**
     * Creates an instance of CheckSubmissionDialog.
     *
     * @param parentShell the parent shell
     * @param versionlist the versionlist
     * @param manager     the manager
     * @param project     the project
     * @param result      the result
     */
    public CheckSubmissionDialog(Shell parentShell, java.util.List<Version> versionlist,
            ExerciseSubmitterManager manager, IProject project, CheckResult result) {
        super(parentShell);
        this.versionlist = versionlist;
        this.manager = manager;
        this.project = project;
        this.checkresult = result;

    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;

        container.setLayout(gridLayout);

        new Label(container, SWT.NULL).setText("Check Result: ");

        Label resultLabel = new Label(container, SWT.RIGHT);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        resultLabel.setLayoutData(gridData);
        String resultString = this.checkresult.getResult() ? "The content is the same" : "The content is NOT the same";
        resultLabel.setText(resultString);

        new Label(container, SWT.NULL).setText("Version TimeStamp: ");
        Label currentVersionLabel = new Label(container, SWT.RIGHT);
        currentVersionLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        String versionString = DateTimeFormatter.ofPattern("dd/MM/YYYY|HH:mm").format(
                this.versionlist.get(0).getTimestamp());
        currentVersionLabel.setText(versionString);

        new Label(container, SWT.NULL).setText("List all Versions: ");

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

        if (!this.checkresult.getResult()) {

            new Label(container, SWT.NULL).setText("Submit current Version: ");

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

            new Label(container, SWT.NULL).setText("Download latest Version: ");

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
     * Callback that is called when the {@link SubmissionJob} has finished. This is
     * always called, even when the submission failed.
     * <p>
     * Displays the submission result to the user.
     *
     * @param job The {@link SubmissionJob} that finished.
     */
    private void onSubmissionFinished(SubmissionJob job) {
        SubmitAction.createSubmissionFinishedDialog(job);

    }

    /**
     * Create a SubmissionJob.
     */
    private void createSubmissionJob() {
        SubmissionJob job;
        try {

            job = new SubmissionJob(this.manager.getSubmitter(this.checkresult.getAssignment()), this.project,
                    this.checkresult.getAssignment(), this.getShell(), this::onSubmissionFinished);
            job.setUser(true);
            job.schedule();
        } catch (IllegalArgumentException | ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Called when replay is finished.
     *
     * @param job
     */
    private void onReplayFinished(ReplayerJob job) {
        System.out.println("Replay success");
    }

    /**
     * Creates an ReplayJob.
     */
    private void createReplayerJob() {
        ReplayerJob job;
        try {

            job = new ReplayerJob(this.getShell(), this.manager.getReplayer(this.checkresult.getAssignment()),
                    this.checkresult.getAssignment(), this::onReplayFinished);
            job.setUser(true);
            job.schedule();
        } catch (IllegalArgumentException | ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    /**
     * Called when list versions is finished.
     *
     * @param job
     */
    private void onListVersionFinished(ListVersionsJob job) {
        System.out.println("List version success");
    }
    /**
     * Creates an ListVerionJob.
     */
    private void createListVersionList() {
        ListVersionsJob job;
        try {

            job = new ListVersionsJob(this.getShell(), this.manager.getReplayer(this.checkresult.getAssignment()),
                    this.checkresult.getAssignment(), this::onListVersionFinished);
            job.setUser(true);
            job.schedule();
        } catch (IllegalArgumentException | ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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
