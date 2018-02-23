package eu.aria.dm.moves;

import org.jeasy.rules.api.Rule;

import java.util.ArrayList;

/**
 * Created by WaterschootJB on 19-6-2017.
 */
public interface Move {

    /**
     * Updating the relevance of the loaded moves. Based on the rules.
     * @param value, the increase or decrease of relevance
     */
    void updateRelevance(double value);

    /**
     * Update the status of the goal
     * @param completed, true if goal is completed
     * @param accomplished, true if goal is accomplished
     * @return if the operation was successful
     */
    boolean updateGoalStatus(boolean completed, boolean accomplished);

    boolean setActor(String name);

    String getName();
    Goal getGoal();
    String getActor();

    ArrayList<String> getSelfForwardGoals();
    ArrayList<String> getOtherForwardGoals();
    ArrayList<String> getOtherExpectedGoals();

    ArrayList<Rule> getRules();


}
