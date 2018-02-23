package eu.aria.dm.kb;

import eu.aria.dm.moves.Move;
import eu.aria.dm.moves.SimpleMove;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.BasicRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.json.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The Knowledge Base consists of moves written in Json format.
 */
public class KBLoader {

    private static Logger logger = LoggerFactory.getLogger(KBLoader.class);
    JsonObject knowledgeBase;

    public KBLoader(String movesPath){
        try {
            String path = this.getClass().getClassLoader().getResource(movesPath).getPath();
            InputStream stream = new FileInputStream(path);
            JsonReader reader = Json.createReader(stream);
            knowledgeBase = reader.readObject().getJsonObject("KnowledgeBase");
            reader.close();
            loadMoves();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    /**
     * Function that loads moves from the JSon file containing moves. Gives errors when name and type are invalid.
     * @return if the operation was successfull.
     */
    public boolean loadMoves(){
        ArrayList<Move> moveList = new ArrayList();
        if(knowledgeBase != null){
            JsonArray episodes = knowledgeBase.getJsonArray("Episode");
            for(int i=0; i<episodes.size();i++){
                JsonObject ep = (JsonObject) episodes.get(i);
                JsonArray exchanges = ep.getJsonArray("Exchange");
                for(int j=0; j<exchanges.size();j++){
                    JsonObject ex = (JsonObject) exchanges.get(j);
                    JsonArray moves = ex.getJsonArray("Move");
                    for(int k=0; k<moves.size();k++){
                        String move_name;
                        String type;
                        String language;
                        ArrayList<Rule> rules;
                        ArrayList<String> utterances;

                        JsonObject move = (JsonObject) moves.get(k);
                        if(move.getJsonString("Goal") != null){
                            move_name = move.getJsonString("Goal").getString();
                        }
                        else{
                            move_name = null;
                            logger.error("Move goal is null");
                        }

                        if(move.getJsonString("Type") != null){
                            type = move.getJsonString("Type").getString();
                        }
                        else{
                            type = null;
                            logger.error("Type is null");
                        }

                        if(move.getJsonString("Language") != null){
                            language = move.getJsonString("Language").getString();
                        }
                        else{
                            logger.warn("Language is undefined, default is English");
                            language = "English";
                        }

                        ArrayList <String> tempRules = (ArrayList<String>) toList(move.getJsonArray("Rules"));
                        rules = new ArrayList();
                        for(String tempRule : tempRules){
                            rules.add(new BasicRule(tempRule));
                        }
                        if(rules.isEmpty()){
                            logger.warn("No rules known");
                        }
                        utterances = (ArrayList<String>) toList(move.getJsonArray("Utterances"));

                        moveList.add(new SimpleMove(move_name, language, utterances, SimpleMove.Type.valueOf(type),rules));
                    }
                }
            }

        }
        return true;
    }

    private static List<String> toList(JsonArray json){
        List<String> list = new ArrayList<String>();
        if(json != null){
            for(JsonString element : json.getValuesAs(JsonString.class)){
                list.add(element.getString());
            }
        }
        return list;
    }

}
