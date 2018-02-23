package eu.aria.dmtools.dialogstructurecreator;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

public class mxExchangeCell extends mxCell {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3458612243165676025L;
	private Exchange exchange = new Exchange();
	
	public Exchange getExchange() {
		return exchange;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}
	
	public mxExchangeCell()
	{
		super(null);
	}

	
	public mxExchangeCell(Object value)
	{
		super(value, null, null);
	}


	public mxExchangeCell(Object value, mxGeometry geometry, String style)
	{
		super (value, geometry,style);		
	}	
}
