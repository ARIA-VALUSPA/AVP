package eu.aria.dm.moves;

import com.sun.istack.NotNull;
import org.jeasy.rules.api.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by WaterschootJB on 19-6-2017.
 */
public class SimpleMove implements Move {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(SimpleMove.class.getName());
    public enum Type {
        CONTENT, INTERACTION, SOCIOEMOTIONAL
    }


    //Contents of the move
    private String name;
    private String actor;
    private Goal goal;
    private double relevance;
    private String language;
    private ArrayList<String> utterances;
    private Episode episode;
    private Exchange exchange;
    private Type moveType;

    //Manual rules for updating the relevance
    private ArrayList<Rule> rules;

    public SimpleMove(@NotNull String name, String language, ArrayList<String> utterances, @NotNull Type moveType, ArrayList<Rule> rules){
        this.name = name;
        String[] names = name.split("\\.");
        this.relevance = 0.0;
        this.language = language;
        this.utterances = utterances;
        this.goal = new Goal(names[3]);
        this.episode = new Episode(new Goal(names[2]));
        this.exchange = new Exchange(new Goal(names[1]));
        this.moveType = moveType;
        this.rules = rules;
    }

    @Override
    public void updateRelevance(double value) {
        this.relevance = value;
        this.episode.setRelevance(this.episode.getRelevance() + 0.1 * value);
        this.exchange.setRelevance(this.exchange.getRelevance() + 0.01 * value);
    }

    @Override
    public boolean updateGoalStatus(boolean completed, boolean accomplished) {
        if(this.getGoal().getStatus() != null){
            this.getGoal().setStatus(completed,accomplished);
            return true;
        }
        logger.error("Goalstatus update not completed successfully, completed: %s, accomplished: %s",completed,accomplished);
        return false;
    }

    @Override
    public boolean setActor(String name) {
        if(name != null){
            this.actor = name;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Goal getGoal() {
        return this.goal;
    }

    @Override
    public String getActor() {
        return this.actor;
    }

    @Override
    public ArrayList<String> getSelfForwardGoals() {
        return this.getSelfForwardGoals();
    }

    @Override
    public ArrayList<String> getOtherForwardGoals() {
        return this.getOtherForwardGoals();
    }

    @Override
    public ArrayList<String> getOtherExpectedGoals() {
        return this.getOtherExpectedGoals();
    }

    @Override
    public ArrayList<Rule> getRules() {
        return this.rules;
    }


}
