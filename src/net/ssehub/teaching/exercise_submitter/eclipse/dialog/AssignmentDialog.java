package net.ssehub.teaching.exercise_submitter.eclipse.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.helper.Converter;
import net.ssehub.teaching.exercise_submitter.helper.Sort;
import net.ssehub.teaching.exercise_submitter.lib.Assignment;

public class AssignmentDialog extends Dialog {

    private java.util.List<Assignment> assignments;
    private Assignment selectedAssignment;
    private Sorted sort;

    public enum Sorted {
        GROUPED, NONE
    }

    public AssignmentDialog(Shell parentShell, java.util.List<Assignment> assignments, Sorted sort) {
        super(parentShell);
        this.assignments = assignments;
        this.selectedAssignment = null;
        this.sort = sort;
    }

    public Assignment getSelectedAssignment() {
        return this.selectedAssignment;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(container,
                SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
        list.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

        if (this.sort == Sorted.GROUPED) {
            this.assignments = Sort.groupByState(this.assignments);
        }
        for (Assignment as : this.assignments) {
            if (this.sort == Sorted.GROUPED) {
                list.add("[" + Converter.assignmentStateToString(as.getState()) + "] " + as.getName());
            } else {
                list.add(as.getName());
            }
        }

        list.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int[] selections = list.getSelectionIndices();
                if (selections.length > 0) {

                    AssignmentDialog.this.selectedAssignment = AssignmentDialog.this.assignments.get(selections[0]);

                } else {
                    // exception
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
