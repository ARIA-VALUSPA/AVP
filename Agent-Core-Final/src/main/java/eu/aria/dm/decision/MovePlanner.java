package eu.aria.dm.decision;

import eu.aria.dm.moves.Move;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by WaterschootJB on 28-5-2017.
 * Class that plans the moves of the agent
 */
public class MovePlanner extends Thread{

    private MoveUpdater mu;
    private MoveSelecter ms;

    private ArrayList<Move> completedAgentMoves;
    private ArrayList<Move> plannedAgentMoves;
    private Queue<Move> plan;

    private ArrayList<Move> observedUserMoves;
    private Move expectedUserMove;
    private ArrayList<Move> expectedUserMoves;

    private ArrayList<Move> possibleMoves;

    private boolean haveToPlan;

    public MovePlanner(ArrayList<Move> allMoves, double threshold){
        this.possibleMoves = allMoves;
        mu = new MoveUpdater(this.possibleMoves, threshold);
        ms = new MoveSelecter();
        this.plan = new LinkedList<>();
        haveToPlan = true;
        this.start();
    }

    public void run(){
        while(haveToPlan){
            if(ms.hasRelevantMove()){
                this.plan.add(ms.getRelevantMove());
            }
        }
    }

    public void stopPlanning(){
        haveToPlan = false;
    }

    public void executePlan(){
        while(haveToPlan){
            if(!plan.isEmpty()){
                plan.peek();
            }

        }
    }



}
