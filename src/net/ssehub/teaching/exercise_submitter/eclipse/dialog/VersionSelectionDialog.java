package net.ssehub.teaching.exercise_submitter.eclipse.dialog;


import java.util.List;
import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.ssehub.teaching.exercise_submitter.eclipse.utils.TimeUtils;
import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer;

/**
 * This class creates a Version dialog. 
 * In which the User can selected out of a List from Replayer.Version
 * @author lukas
 *
 */
public class VersionSelectionDialog extends Dialog {
    
    private List<Replayer.Version> versionlist;
    private Optional<Replayer.Version> selectedVersion;

    /**
     * Creates a Dialog which display a list of Versions.
     * @param parentShell , the parent shell
     * @param versionlist , the list of the version which should be displayed
     */
    public VersionSelectionDialog(Shell parentShell, List<Replayer.Version> versionlist) {
        super(parentShell);
        this.versionlist = versionlist;
        this.selectedVersion = Optional.empty();
    }
    /**
     * Returns an Optional of the selected Version.
     * @return Optional Replayer.Version , The Selected Version or null.
     */
    public Optional<Replayer.Version> getSelectedAssignment() {
        return this.selectedVersion;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

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
            tc.setResizable(false);
        }

        for (Replayer.Version version : this.versionlist) {

            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, TimeUtils.instantToLocalString(version.getTimestamp()));
            item.setText(1, version.getAuthor());
          
        }

        for (TableColumn cm : table.getColumns()) {
            cm.pack();
        }
        table.setSize(table.computeSize(SWT.DEFAULT, 200));

        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int[] selections = table.getSelectionIndices();
                if (selections.length == 1) {
                    VersionSelectionDialog.this.selectedVersion = Optional
                            .of(VersionSelectionDialog.this.versionlist.get(selections[0]));
                }
            }
        });

        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Selecting Version");
    }

    @Override
    protected Point getInitialSize() {
        return new Point(280, 200);
    }
    

}
