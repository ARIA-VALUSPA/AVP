package eu.aria.dmtools.dialogstructurecreator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

public class DialogStructureData {
	
	private Gson dsd;
	
	private String test = "oh yeah this is a test";
	
	public void createDSD(){
		this.dsd = dsd;
	}
	
	public Gson getDSD(){
		return this.dsd;
	}
	
	public void setDSD(Gson dsd){
		this.dsd = dsd;
	}
	
	public String getTest(){
		return this.test;
	}
	
	public void setTest(String test){
		this.test = test;
	}
	
		
	public void loadDSfromfile(String filelocation) throws IOException{
		
	}
	
}
