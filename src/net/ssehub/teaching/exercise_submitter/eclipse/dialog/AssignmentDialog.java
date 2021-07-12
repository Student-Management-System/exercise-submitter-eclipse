package net.ssehub.teaching.exercise_submitter.eclipse.dialog;

import java.util.Comparator;
import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.lib.Assignment;

/**
 * A dialog for showing a list of {@link Assignment}s. Also allows the user to select one.
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
     * @param sort The sorting of the assignments.
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

        final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(container,
                SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
        list.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

        if (this.sort == Sorted.GROUPED) {
            this.assignments.sort(
                    Comparator.comparing((Assignment a) -> a.getState().ordinal())
                    .thenComparing(Comparator.comparing(a -> a.getName()))); // TODO: maybe better sorting?
        }
        for (Assignment as : this.assignments) {
            if (this.sort == Sorted.GROUPED) {
                list.add("[" + as.getState().name() + "] " + as.getName());
            } else {
                list.add(as.getName());
            }
        }

        list.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int[] selections = list.getSelectionIndices();
                if (selections.length == 1) {
                    selectedAssignment = Optional.of(assignments.get(selections[0]));
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
        return new Point(300, 200);
    }
}
