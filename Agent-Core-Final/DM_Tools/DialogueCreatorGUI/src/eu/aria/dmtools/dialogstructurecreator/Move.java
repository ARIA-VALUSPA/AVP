package eu.aria.dmtools.dialogstructurecreator;

import java.io.Serializable;
import java.util.ArrayList;

public class Move implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6933175195089110211L;
	private String celltype = "Move";
	private String name;
	private String uu;
	private String au;
	private String rules;
		
	public Move(String name, String uu, String au, String rules){
		this.celltype = "Move";
		this.name = name;
		this.uu = uu;
		this.au = au;
		this.rules = rules;		
	}
	
	public Move(){
		celltype = "Move";
		name = "Move";
		uu = "test";
		au = "";
		rules = "";
	}	
	
	public String getCellType() {
		return celltype;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUu() {
		return uu;
	}

	public void setUu(String uu) {
		this.uu = uu;
	}

	public String getAu() {
		return au;
	}

	public void setAu(String au) {
		this.au = au;
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}
	
	@Override
	public String toString() {
		return ("[Move] "+this.name);
	}
	
}
