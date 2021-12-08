package net.ssehub.teaching.exercise_submitter.eclipse.dialog;


import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import net.ssehub.teaching.exercise_submitter.lib.replay.Replayer.Version;

/**
 * A dialog for showing versions. Does not allow selection.
 * 
 * @author Adam
 *
 */
public class VersionListDialog extends VersionSelectionDialog {
    
    /**
     * Creates a Dialog which display a list of Versions.
     * @param parentShell , the parent shell
     * @param assignmentName The name of the assignment that the versions are displayed for.
     * @param versionlist , the list of the version which should be displayed
     */
    public VersionListDialog(Shell parentShell, String assignmentName, List<Version> versionlist) {
        super(parentShell, assignmentName, versionlist);
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, CANCEL, "Close", true);
    }
    
    @Override
    protected String getTitle() {
        return "Versions of " + assignmentName;
    }

}
