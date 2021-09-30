package net.ssehub.teaching.exercise_submitter.eclipse.dialog;

import java.time.format.DateTimeFormatter;

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

import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer.Version;

/**
 * This creates the CheckSubmissionDialog.
 * 
 * @author lukas
 *
 */
public class CheckSubmissionDialog extends Dialog {

    private java.util.List<Version> versionlist;
    private boolean checkResult;
    /**
     * Creates an instance of CheckSubmissionDialog.
     * @param parentShell
     * @param versionlist
     * @param checkResult
     */
    public CheckSubmissionDialog(Shell parentShell, java.util.List<Version> versionlist, boolean checkResult) {
        super(parentShell);
        this.versionlist = versionlist;
        this.checkResult = checkResult;

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
        String resultString = this.checkResult ? "The content is the same" : "The content is NOT the same";
        resultLabel.setText(resultString);

        new Label(container, SWT.NULL).setText("Version TimeStamp: ");
        Label currentVersionLabel = new Label(container, SWT.RIGHT);
        currentVersionLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        String versionString = this.versionlist.get(0).getTimestamp()
                .format(DateTimeFormatter.ofPattern("dd/MM/YYYY|HH:mm"));
        currentVersionLabel.setText(versionString);

        new Label(container, SWT.NULL).setText("List all Versions: ");

        Button listVersionsButton = new Button(container, SWT.PUSH);
        listVersionsButton.setText("List Versions");
        listVersionsButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        listVersionsButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                System.out.println("list versions");
            }
        });

        if (!this.checkResult) {

            new Label(container, SWT.NULL).setText("Submit current Version: ");

            Button submitButton = new Button(container, SWT.PUSH);
            submitButton.setText("Submit");
            submitButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
            submitButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    System.out.println("Submit current Version");
                }
            });

            new Label(container, SWT.NULL).setText("Download latest Version: ");

            Button downloadButton = new Button(container, SWT.PUSH);
            downloadButton.setText("Download");
            downloadButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
            downloadButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    System.out.println("Download latest Version");
                }
            });
        }

        return container;
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
