package eu.aria.dm.kb;

import eu.aria.dm.moves.Move;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by WaterschootJB on 4-7-2017.
 */
public class Dialogue implements Serializable{

    ArrayList<Move> moves;

    public Dialogue(){
        this.moves = new ArrayList();
    }

    public ArrayList<Move> getMoves() {
        return moves;
    }

    public void setMoves(ArrayList<Move> moves) {
        this.moves = moves;
    }

}
