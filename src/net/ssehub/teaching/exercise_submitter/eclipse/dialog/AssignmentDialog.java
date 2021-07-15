package net.ssehub.teaching.exercise_submitter.eclipse.dialog;

import java.util.ArrayList;
import java.util.Comparator;
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

import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;

/**
 * A dialog for showing a list of {@link Assignment}s. Also allows the user to
 * select one.
 * 
 * @author Lukas
 */
public class AssignmentDialog extends Dialog {

    private java.util.List<Assignment> assignments;
    private Optional<Assignment> selectedAssignment;
    private Sorted sort;

    /**
     * Sorting options for assignments.
     */
    public enum Sorted {
        GROUPED, NONE
    }

    /**
     * Creates a dialog with the given assignments.
     *
     * @param parentShell The parent shell to open this dialog for.
     * @param assignments The assignments to display.
     * @param sort        The sorting of the assignments.
     */
    public AssignmentDialog(Shell parentShell, java.util.List<Assignment> assignments, Sorted sort) {
        super(parentShell);
        this.assignments = assignments;
        this.selectedAssignment = Optional.empty();
        this.sort = sort;
    }

    /**
     * Returns the assignment selected by the user.
     *
     * @return The assignment that was selected by the user.
     */
    public Optional<Assignment> getSelectedAssignment() {
        return this.selectedAssignment;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        Table table = new Table(container, SWT.BORDER | SWT.MULTI);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        FillLayout rw = new FillLayout();

        table.setSize(table.computeSize(SWT.DEFAULT, 200));
        table.setLayout(rw);
        container.setLayout(rw);

        String[] colNames = {"Name", "State", "Group Work"};

        for (String s : colNames) {
            TableColumn tc = new TableColumn(table, SWT.NONE);
            tc.setText(s);
            tc.setResizable(false);
        }

        if (this.sort == Sorted.GROUPED) {
            this.assignments = new ArrayList<>(this.assignments); // create copy so that it can be sorted
            this.assignments.sort(Comparator.comparing((Assignment a) -> a.getState().ordinal())
                    .thenComparing(Comparator.comparing(Assignment::getName))); // TODO: maybe better sorting?
        }

        for (Assignment as : this.assignments) {

            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, as.getName());
            item.setText(1, as.getState().toString());
            item.setText(2, String.valueOf(as.isGroupWork()));

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
                    AssignmentDialog.this.selectedAssignment = Optional
                            .of(AssignmentDialog.this.assignments.get(selections[0]));
                }
            }
        });

        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Selecting Assignment");
    }

    @Override
    protected Point getInitialSize() {
        return new Point(280, 200);
    }
}
