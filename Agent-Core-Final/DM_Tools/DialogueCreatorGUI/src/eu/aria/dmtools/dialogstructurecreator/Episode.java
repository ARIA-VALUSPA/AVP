package eu.aria.dmtools.dialogstructurecreator;

import java.io.Serializable;

public class Episode implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4885229683845569869L;
	private String name;
	
	public Episode(){
		name = "Episode";
	}
	
	public Episode(String name){
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
		return ("[Episode] "+this.name);
	}
}
