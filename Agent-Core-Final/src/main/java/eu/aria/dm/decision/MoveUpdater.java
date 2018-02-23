package eu.aria.dm.decision;

import eu.aria.dm.moves.Move;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by WaterschootJB on 4-7-2017.
 */
public class MoveUpdater {

    public double threshold = 0.7;
    private ArrayList<Move> agentMoves;

    public MoveUpdater(ArrayList<Move> moves, double threshold){
        if(threshold == 0.0){
            new MoveUpdater(moves);
        }
        else{
            this.agentMoves = moves;
            this.threshold = threshold;
        }
    }

    public MoveUpdater(ArrayList<Move> moves){
        new MoveUpdater(moves, this.threshold);
    }

    /**
     * Checks all the moves for their preconditions and updates the corresponding relevance
     */
    public void updateRelevance(){
        for(Move m : agentMoves){
            for(Rule r : m.getRules()){
                double relevance = 0.0;
                if(r.evaluate(new Facts())){
                    relevance = 0.01;
                    m.updateRelevance(relevance);
                }
            }
        }
    }

    /**
     * Update the threshold for firing a move
     */
    public void setThreshold(double threshold){
        this.threshold = threshold;
    }
}
