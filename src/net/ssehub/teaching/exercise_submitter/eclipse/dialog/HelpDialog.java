package net.ssehub.teaching.exercise_submitter.eclipse.dialog;

import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;

import net.ssehub.teaching.exercise_submitter.eclipse.background.AbstractJob;
import net.ssehub.teaching.exercise_submitter.eclipse.background.AuthenticateJob;
import net.ssehub.teaching.exercise_submitter.eclipse.background.GetAssignmentsJob;
import net.ssehub.teaching.exercise_submitter.lib.data.Course;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.ApiException;
import net.ssehub.teaching.exercise_submitter.lib.student_management_system.IApiConnection;

/**
 * A dialog showing information about this plug-in.
 * 
 * @author Adam
 */
public class HelpDialog extends Dialog {

    /**
     * Creates a new help dialog.
     * 
     * @param parentShell The parent shell.
     */
    public HelpDialog(Shell parentShell) {
        super(parentShell);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 10;
        layout.horizontalSpacing = 8;
        container.setLayout(layout);

        Label courseLabel = createLabel(container, "Course:");
        Label usernameLabel = createLabel(container, "Username:");
        Label roleLabel = createLabel(container, "Role:");
        Label submissionAssignmentsLabel = createLabel(container, "Assignments open\nfor submission:");
        Label replayAssignmentsLabel = createLabel(container, "Assignments open\nfor download:");
        
        Label versionLabel = createLabel(container, "Plug-in version:");
        setLabelText(versionLabel, FrameworkUtil.getBundle(getClass()).getVersion().toString());
        
        new AuthenticateJob(getParentShell(), manager -> {
            Course course = manager.getCourse();
            setLabelText(courseLabel, course.getName() + "\n(" + course.getId() + ")");
            
            IApiConnection api = manager.getStudentManagementConnection();
            setLabelText(usernameLabel, api.getUsername());
            
            new AbstractJob<String>("Retrieve role of user " + api.getUsername() + " in course " + course.getId(),
                    getParentShell(), role -> setLabelText(roleLabel, role)) {
                    @Override
                    protected Optional<String> run() {
                        return Optional.of(getRoleName(api, course));
                    }
            }.schedule();
            
            new GetAssignmentsJob(getParentShell(), manager, GetAssignmentsJob.NO_FILTER, assignments -> {
                setLabelText(submissionAssignmentsLabel, String.valueOf(assignments.stream()
                        .filter(manager::isSubmittable)
                        .count()));
                setLabelText(replayAssignmentsLabel, String.valueOf(assignments.stream()
                        .filter(manager::isReplayable)
                        .count()));
            }).schedule();
            
        }).schedule();
        
        return container;
    }
    
    /**
     * Sets the text for the given label. This also removes the italic slant from the labels font.
     * 
     * @param label The label to set the text for.
     * @param text The new text for the label.
     */
    private void setLabelText(Label label, String text) {
        label.setText(text);
        setItalic(label, false);
        if (getShell().isVisible()) {
            getShell().pack();
        }
    }
    
    /**
     * Creates a label with the given description label prefixing it.
     * 
     * @param container The container to add the two labels to.
     * @param name The description of the label.
     * 
     * @return The created label.
     */
    private Label createLabel(Composite container, String name) {
        Label nameLabel = new Label(container, SWT.RIGHT);
        nameLabel.setText(name);
        nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
        
        Label valueLabel = new Label(container, SWT.NONE);
        valueLabel.setText("Loading...");
        setItalic(valueLabel, true);
        
        return valueLabel;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, OK, "Close", true);
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Exercise Submitter");
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }
    
    /**
     * Sets the given labels text to italic or non-italic.
     * 
     * @param label The label to set the text font for.
     * @param italic Whetehr the font should be italic or not.
     */
    private static void setItalic(Label label, boolean italic) {
        Font font = label.getFont();
        FontData fontdata = font.getFontData()[0];
        
        if (italic) {
            fontdata.setStyle(fontdata.getStyle() | SWT.ITALIC);
        } else {
            fontdata.setStyle(fontdata.getStyle() & ~SWT.ITALIC);
        }
        
        label.setFont(new Font(font.getDevice(), fontdata));
    }
    
    /**
     * Returns a string representation of the role of the logged-in user in the given course.
     * 
     * @param connection The connection with a logged-in user.
     * @param course The course to get the the role for.
     * 
     * @return The role of the user.
     */
    private static String getRoleName(IApiConnection connection, Course course) {
        String role;
        boolean tutor;
        try {
            tutor = connection.hasTutorRights(course);
            if (tutor) {
                role = "Tutor";
            } else {
                role = "Student";
            }
        } catch (ApiException e) {
            role = "(error)";
        }
        return role;
    }
    
}
