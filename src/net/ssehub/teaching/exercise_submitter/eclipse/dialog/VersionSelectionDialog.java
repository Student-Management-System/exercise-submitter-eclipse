package net.ssehub.teaching.exercise_submitter.eclipse.dialog;


import java.util.List;
import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.ssehub.teaching.exercise_submitter.eclipse.utils.TimeUtils;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer.Version;

/**
 * A dialog which shows a list of versions and allows the user to select one (or cancel).
 * 
 * @author lukas
 */
public class VersionSelectionDialog extends Dialog {
    
    protected String assignmentName;
    
    private List<Version> versionlist;
    
    private Optional<Version> selectedVersion;

    /**
     * Creates a dialog which displays the given list of versions.
     * 
     * @param parentShell The parent shell
     * @param assignmentName The name of the assignment that the versions are displayed for.
     * @param versionlist The list of versions to display.
     */
    public VersionSelectionDialog(Shell parentShell, String assignmentName, List<Version> versionlist) {
        super(parentShell);
        this.assignmentName = assignmentName;
        this.versionlist = versionlist;
        this.selectedVersion = Optional.empty();
    }
    
    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
        if (id == OK) {
            button.setEnabled(false);
        }
        return button;
    }
    
    /**
     * The version selected by the user.
     * 
     * @return The version selected by the user, or {@link Optional#empty()} if the user cancelled.
     */
    public Optional<Version> getSelectedAssignment() {
        return this.selectedVersion;
    }
    
    /**
     * Opens this dialog and returns the user-selected version.
     *
     * @return The version selected by the user, or {@link Optional#empty()} if the user cancelled.
     */
    public Optional<Version> openAndGetSelectedVersion() {
        int result;
        do {
            result = open();
            
            // if user pressed ok but did not select an assignment, just open again.
        } while (result == OK && this.selectedVersion.isEmpty());
        
        if (result != OK) {
            this.selectedVersion = Optional.empty();
        }
        
        return this.selectedVersion;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        if (!versionlist.isEmpty()) {
            createVersionTable(container);
            
        } else {
            container.setLayout(new FillLayout());
            Label noVersions = new Label(parent, SWT.CENTER);
            noVersions.setText("No versions have been submitted yet.");
        }

        return container;
    }
    
    /**
     * Creates a table with the versions.
     * 
     * @param container The container to add the table to.
     */
    private void createVersionTable(Composite container) {
        Table table = new Table(container, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
        table.setLinesVisible(false);
        table.setHeaderVisible(true);
        FillLayout rw = new FillLayout();

        table.setSize(table.computeSize(SWT.DEFAULT, 200));
        table.setLayout(rw);
        container.setLayout(rw);

        String[] colNames = {"Timestamp", "Author"};

        for (String s : colNames) {
            TableColumn tc = new TableColumn(table, SWT.NONE);
            tc.setText(s);
            tc.setResizable(true);
        }

        for (Version version : this.versionlist) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, TimeUtils.instantToLocalString(version.getTimestamp()));
            item.setText(1, version.getAuthor());
        }
        table.getItem(0).setText(0, table.getItem(0).getText(0) + " (latest)");

        for (TableColumn cm : table.getColumns()) {
            cm.pack();
        }
        table.setSize(table.computeSize(SWT.DEFAULT, 200));

        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int[] selections = table.getSelectionIndices();
                Optional<Button> okButton = Optional.ofNullable(getButton(OK));
                
                if (selections.length == 1) {
                    okButton.ifPresent(button -> button.setEnabled(true));
                    
                    VersionSelectionDialog.this.selectedVersion = Optional
                            .of(VersionSelectionDialog.this.versionlist.get(selections[0]));
                } else {
                    okButton.ifPresent(button -> button.setEnabled(false));
                }
            }
        });
    }

    /**
     * Returns the title of this dialog.
     * 
     * @return The dialog title.
     */
    protected String getTitle() {
        return "Select Version of " + assignmentName;
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getTitle());
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }

}
