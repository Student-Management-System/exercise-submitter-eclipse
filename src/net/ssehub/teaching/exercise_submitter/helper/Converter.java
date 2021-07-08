package net.ssehub.teaching.exercise_submitter.helper;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import net.ssehub.teaching.exercise_submitter.lib.Assignment;
import net.ssehub.teaching.exercise_submitter.lib.Problem.Severity;

public class Converter {
    public static IMarker getIMarkerSeverity(Severity sev, IMarker im) {
        try {
            switch (sev) {
            case WARNING:
                im.setAttribute(IMarker.SEVERITY, IMarker.PRIORITY_NORMAL);
                break;
            case ERROR:
                im.setAttribute(IMarker.SEVERITY, IMarker.PRIORITY_HIGH);
                break;

            }
        } catch (CoreException e) {

        }

        return null; // exception
    }

    public static String assignmentStateToString(Assignment.State state) {
        String sState = null;
        switch (state) {
        case SUBMISSION:
            sState = "SUBMISSION";
            break;
        case REVIEWED:
            sState = "REVIEWED";
            break;
        case IN_REVIEW:
            sState = "IN_REVIEW";
            break;
        default:
            break;
        }
        return sState;
    }
}
