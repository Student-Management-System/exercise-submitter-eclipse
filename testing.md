
# Menu Bar -> Configure

- configure in menu bar opens preference page
- login with wrong username or password shows error dialog on apply
- login with correct username or password does not show a dialog

# Menu Bar -> Show Assignments

- shows error dialog if not logged in
- shows list of assignments
- close just closes the dialog and does nothing
- selecting an assignment allows viewing the version history
    - version history dialog does not allow selection of a verison (can only be closed)
    - if not replayable (e.g. IN_REVIEW) shows that assignment can currently not be accessed

# Menu Bar -> Help

- opens dialog
    - shows plug-in version
    - if logged in
        - shows course, username, role
    - if not logged in
        - shows "not yet logged in"

# Context Menu -> Submit

- shows error dialog if not logged in
- shows warning dialog if error or warning markers are in project
    - cancel cancels the submission

- if no assignment is associated yet    
    - shows assignment selection dialog with only assignment in SUBMISSION
    - clicking cancel cancels the submission
- if assignment is associated
    - asks if associated assignment should be used again or if another should be selected (see if no assignment is associated yet)
- if assignment is associated but not submittable anymore
    - ask the user to choose a different assignment (see if no assignment is associated yet)
    
- submits the project
- shows submission result
    - accepted and no markers -> everything ok
    - accepted but with markers -> accepted, but markers where added to project
    - not accepted -> clear error message
- shows assignment name next to project (as a project label) if submission was accepted

# Context Menu -> Check Submission

- shows error dialog if not logged in

- if no assignment is associated yet    
    - shows assignment selection dialog
    - clicking cancel cancels the operation
- if assignment is associated
    - asks if associated assignment should be used again or if another should be selected (see if no assignment is associated yet)
- if assignment is associated but not replayable anymore
    - ask the user to choose a different assignment (see if no assignment is associated yet)

- shows warning dialog if no submission has been made yet

- shows result of comparison
    -if content is equal
        - "content is the same"
        - latest version timestamp
        - button for list all versions (no selection in version list dialog)
    - if content is not equal
        - "content differs"
        - latest version timestamp
        - button for list all versions (no selection in version list dialog)
        - button for submit local project
            - only if assignment is in SUBMISSION
        - button for download latest version

# Context Menu -> Show Version history

- shows error dialog if not logged in

- if an assignment is associated to the project
    - shows a dialog with the version history
- else
    - shows an info dialog that versions of other assignments can be viewed under "Show Assignmnets" in menu bar

# Context Menu -> Clear Problem Markers

- clears only our markers from previous submission

# Menu Bar -> Download Submission

- shows error dialog if not logged in

- shows assignment selection dialog with all replayable assignments (SUBMISSION or REVIEWED)
    - cancel cancels the operation

- shows version selection dialog
    - cancel cancels the operation

- if a project with the target name already exists, shows a confirmation dialog to override
    - allows to cancel the replay
    - confirmation deletes all previous content of the existing project

- downloads the submission
    - creates a project with submission name and timestamp
    - creates a .project with javanature and default .classpath
    - copies submission content into new project
    - marks project content as read-only

- shows a dialog with the newly created project name
- shows assignment name as project label


# TODO

- Version list dialog with no versions shows special message
