package eu.aria.dm.util;

import javax.json.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for alignment module
 */
public class ModifiedSentence {
    public String modifiedSentence;
    public String type; //baseline, best_convergent, most_divergent, convergent, divergent
    private Map<String, Double> measures = new HashMap<>();
 
    public Map<String, Double> getMeasures() {
        return measures;
    }
	
    public void addMeasure(String name, double d) {
    	measures.put(name, d);
    }

    public ModifiedSentence(JsonObject modifiedSentence){
        this.modifiedSentence = modifiedSentence.getString("modifiedSentence");
        this.type = modifiedSentence.getString("type");
        JsonObject measures = modifiedSentence.getJsonObject("measures");
        Set<String> keys = measures.keySet();
        for(String key : keys){
            addMeasure(key,measures.getJsonNumber(key).doubleValue());
        }

    }

    @Override
    public String toString() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        measures.forEach((key, value) -> job.add(key, value));
        JsonObject mj = job.build();

        return Json.createObjectBuilder()
                .add("modifiedSentence",this.modifiedSentence)
                .add("type",this.type)
                .add("measures",mj)
                .build().toString();
    }
}
