package net.ssehub.teaching.exercise_submitter.eclipse.helper;

import net.ssehub.teaching.exercise_submitter.lib.Assignment;

public class Converter {

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
