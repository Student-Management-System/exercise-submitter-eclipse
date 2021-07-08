package net.ssehub.teaching.exercise_submitter.eclipse.eclipsemanager;

import net.ssehub.teaching.exercise_submitter.eclipse.user.UserHandler;
import net.ssehub.teaching.exercise_submitter.lib.Manager;

public class EclipseManager {

    private Manager manager;

    public EclipseManager() {
        this.checkUserdata();
    }

    private void checkUserdata() {
        if (UserHandler.getUsername() != null && UserHandler.getPassword() != null) {
            this.manager = new Manager(UserHandler.getUsername(), UserHandler.getPassword().toCharArray());
        }
    }

    public Manager getManager() {
        if (this.manager == null) {
            // throw exception
        }
        return this.manager;
    }
}
