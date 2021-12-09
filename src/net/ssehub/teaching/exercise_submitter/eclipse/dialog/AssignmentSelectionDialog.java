package net.ssehub.teaching.exercise_submitter.eclipse.dialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.IWorkbenchWindow;

import net.ssehub.teaching.exercise_submitter.eclipse.labels.ProjectAssignmentMapper;
import net.ssehub.teaching.exercise_submitter.lib.ExerciseSubmitterManager;
import net.ssehub.teaching.exercise_submitter.lib.data.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.AuthenticationException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.NetworkException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.UserNotInCourseException;

/**
 * A dialog that allows the user to select an item from a given list of {@link Assignment}s (or to cancel).
 * 
 * @author Lukas
 * @author Adam
 */
public class AssignmentSelectionDialog extends Dialog {

    private List<Assignment> assignments;
    private Optional<Assignment> selectedAssignment;

    private Optional<String> okButtonText = Optional.empty();
    private Optional<String> cancelButtonText = Optional.empty();
    
    /**
     * Creates a dialog with the given assignments.
     *
     * @param parentShell The parent shell to open this dialog for.
     * @param assignments The assignments to display.
     */
    public AssignmentSelectionDialog(Shell parentShell, List<Assignment> assignments) {
        super(parentShell);
        this.assignments = assignments;
        this.selectedAssignment = Optional.empty();
    }
    
    /**
     * Changes the text content of the buttons.
     * 
     * @param ok The new ok button text.
     * @param cancel The new cancel button text.
     */
    public void setButtonTexts(String ok, String cancel) {
        this.okButtonText = Optional.of(ok);
        this.cancelButtonText = Optional.of(cancel);
    }
    
    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        if (id == OK && okButtonText.isPresent()) {
            label = okButtonText.get();
        }
        if (id == CANCEL && cancelButtonText.isPresent()) {
            label = cancelButtonText.get();
        }
        Button button = super.createButton(parent, id, label, defaultButton);
        if (id == OK) {
            button.setEnabled(false);
        }
        return button;
    }

    /**
     * Opens this dialog and returns the user-selected assignment.
     *
     * @return The assignment that was selected by the user.
     */
    public Optional<Assignment> openAndGetSelectedAssignment() {
        int result;
        do {
            result = open();
            
            // if user pressed ok but did not select an assignment, just open again.
        } while (result == OK && this.selectedAssignment.isEmpty());
        
        if (result != OK) {
            this.selectedAssignment = Optional.empty();
        }
        
        return this.selectedAssignment;
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        if (!assignments.isEmpty()) {
            createAssignmentTable(container);
            
        } else {
            container.setLayout(new FillLayout());
            Label noVersions = new Label(parent, SWT.CENTER);
            noVersions.setText("No assignments available.");
        }

        return container;
    }
    
    /**
     * Creates the table with the assignments.
     * 
     * @param container The container to add the table to.
     */
    private void createAssignmentTable(Composite container) {
        Table table = new Table(container, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
        table.setLinesVisible(false);
        table.setHeaderVisible(true);
        FillLayout rw = new FillLayout();

        table.setSize(table.computeSize(SWT.DEFAULT, 200));
        table.setLayout(rw);
        container.setLayout(rw);

        String[] colNames = {"Name", "State", "Type"};

        for (String s : colNames) {
            TableColumn tc = new TableColumn(table, SWT.NONE);
            tc.setText(s);
            tc.setResizable(true);
        }

        this.assignments = new ArrayList<>(this.assignments); // create copy so that it can be sorted
        this.assignments.sort(Comparator.comparing((Assignment a) -> a.getState().ordinal())
                .thenComparing(Comparator.comparing(Assignment::getName))); // TODO: maybe better sorting?

        for (Assignment as : this.assignments) {

            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, as.getName());
            item.setText(1, as.getState().toString());
            item.setText(2, as.isGroupWork() ? "Group" : "Single");

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
                    AssignmentSelectionDialog.this.selectedAssignment = Optional
                            .of(AssignmentSelectionDialog.this.assignments.get(selections[0]));
                    getButton(OK).setEnabled(true);
                } else {
                    getButton(OK).setEnabled(false);
                }
            }
        });
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Select Assignment");
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Lets the user chose the assignment for the given project. If an assignment has been associated with this project,
     * the user is first offered to use the associated assignment (if it fulfills the given predicate).
     * 
     * @param project The project to chose the assignment for.
     * @param window The current window.
     * @param manager The {@link ExerciseSubmitterManager} to get assignments from.
     * @param accessibleCheck A predicate to check if the assignment is accessible. This usually checks if the
     *      assignment is submittable or replayable (depending on the need of the caller). If an assignment is
     *      associated to this project, it is only used if this predicate returns <code>true</code>. If an
     *      {@link AssignmentSelectionDialog} is opened, it only shows {@link Assignment}s for which this predicate is
     *      <code>true</code>.
     * 
     * @return The selected {@link Assignment}, or {@link Optional#empty()} if the user cancelled the operation.
     * 
     * @throws ApiException
     */
    public static Optional<Assignment> selectAssignmentWithAssociated(IProject project, IWorkbenchWindow window,
            ExerciseSubmitterManager manager, Predicate<Assignment> accessibleCheck)
            throws NetworkException, AuthenticationException, UserNotInCourseException, ApiException {
        
        Optional<Assignment> assignment = ProjectAssignmentMapper.INSTANCE.getAssociatedAssignment(project, manager);
        
        assignment = assignment.flatMap(saved -> {
            Optional<Assignment> result;
            if (accessibleCheck.test(saved)) {
                result = Optional.of(saved);
            } else {
                MessageDialog.openInformation(window.getShell(), "Assignment Not Accessible", "The project was last "
                        + "submitted to " + saved.getName() + ", but this assignment is currently not accessible."
                        + " Choose a different one.");
                result = Optional.empty();
            }
            return result;
        }).flatMap(saved -> {
            int chosen = MessageDialog.open(MessageDialog.QUESTION, window.getShell(), "Choose Assignment",
                    "This project was last submitted to " + saved.getName() + ". Use this assignment?",
                    SWT.NONE, saved.getName(), "Different Assignment");
            
            return chosen == 0 ? Optional.of(saved) : Optional.empty();
        });
        
        if (assignment.isEmpty()) {
            AssignmentSelectionDialog assDialog = new AssignmentSelectionDialog(window.getShell(),
                    manager.getAllAssignments().stream().filter(accessibleCheck).collect(Collectors.toList()));
            
            assignment = assDialog.openAndGetSelectedAssignment();
        }
        
        return assignment;
    }
    
}
