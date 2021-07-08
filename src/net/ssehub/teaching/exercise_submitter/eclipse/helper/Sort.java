package net.ssehub.teaching.exercise_submitter.eclipse.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.ssehub.teaching.exercise_submitter.lib.Assignment;

public class Sort {

    public static List<Assignment> groupByState(List<Assignment> list) {
        List<Assignment> sorted = new ArrayList<Assignment>();

        for (int i = 0; i < 3; i++) {
            Assignment.State currentState = null;
            switch (i) {
            case 0:
                currentState = Assignment.State.SUBMISSION;
                break;
            case 1:
                currentState = Assignment.State.IN_REVIEW;
                break;
            case 2:
                currentState = Assignment.State.REVIEWED;
                break;

            }
            sorted.addAll(getAssignmentsFromState(list, currentState));
        }
        return sorted;
    }

    public static List<Assignment> getAssignmentsFromState(List<Assignment> list, Assignment.State state) {
        return list.stream().filter(ass -> ass.getState() == state).collect(Collectors.toList());
    }

}
