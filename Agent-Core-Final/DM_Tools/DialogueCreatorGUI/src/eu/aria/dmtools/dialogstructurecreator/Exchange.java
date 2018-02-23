package eu.aria.dmtools.dialogstructurecreator;

import java.io.Serializable;

public class Exchange implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3662847752254434610L;
	private String name;
	
	public Exchange(){
		name = "Exchange";
	}
	
	public Exchange(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return ("[Exchange] "+this.name);
	}
}
