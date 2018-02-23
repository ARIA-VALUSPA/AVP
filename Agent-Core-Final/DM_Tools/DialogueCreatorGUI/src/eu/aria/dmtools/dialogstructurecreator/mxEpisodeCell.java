package eu.aria.dmtools.dialogstructurecreator;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

public class mxEpisodeCell extends mxCell {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8458597741403612838L;
	private Episode episode = new Episode();
	
	public Episode getEpisode() {
		return episode;
	}

	public void setEpisode(Episode episode) {
		this.episode = episode;
	}
	
	public mxEpisodeCell()
	{
		super(null);
	}

	
	public mxEpisodeCell(Object value)
	{
		super(value, null, null);
	}


	public mxEpisodeCell(Object value, mxGeometry geometry, String style)
	{
		super (value, geometry, style);		
	}	

}
