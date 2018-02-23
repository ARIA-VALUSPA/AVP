package eu.aria.dmtools.dialogstructurecreator;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

public class mxMoveCell extends mxCell {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -742884923628370758L;
	private Move move = new Move();

	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	
	
	public mxMoveCell()
	{
		super(null);
	}

	
	public mxMoveCell(Object value)
	{
		super(value, null, null);
	}


	public mxMoveCell(Object value, mxGeometry geometry, String style)
	{
		super (value, geometry,style);		
	}
	
}
