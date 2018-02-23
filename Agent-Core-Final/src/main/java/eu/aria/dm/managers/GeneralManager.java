package eu.aria.dm.managers;

import eu.aria.dm.kb.KBLoader;
import eu.aria.dm.util.Feedback;
import eu.aria.util.activemq.SimpleReceiverWrapper;
import eu.aria.util.activemq.util.UrlBuilder;
import hmi.flipper2.FlipperException;
import hmi.flipper2.TemplateController;
import hmi.flipper2.postgres.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.ArrayList;

/**
 * Created by WaterschootJB on 2-6-2017.
 */
public class GeneralManager extends SimpleManager{

    private static Logger logger = LoggerFactory.getLogger(GeneralManager.class.getName());
    private TemplateController tc;
    private ArrayList<Manager> managers = new ArrayList<>();
    private Database db;
    private SimpleReceiverWrapper listener;
    private String port = "61616";
    private String host = "localhost";
    private boolean isTopic = true;
    private String destination = "SSI";

    public GeneralManager() throws FlipperException, InterruptedException {

//        db = null;
//        tc = TemplateController.create("ARIA TC","ARIA Dialogue Structuring",db);
//        tc.addTemplateFile(tc.resourcePath("templates/ARIA.xml"));
//        if(db!=null){
//            db.commit();
//        }
//        InputManager tm = new InputManager();
//        Feedback fb = new Feedback();
//        GUIManager gm = new GUIManager();
//        KBLoader kbl = new KBLoader("data/moves.json");
//        //FMLManager fm = new FMLManager();
//        //FMLGenerator fg = new FMLGenerator(fm);
//
        listener = new SimpleReceiverWrapper(UrlBuilder.getUrlTcp(host,port),destination,isTopic);
        listener.start((Message message) ->{
            if(message instanceof TextMessage){
                try{
                    logger.info("Information state: {}",tc.getIs("is.states").toString());
                } catch (FlipperException e) {
                    e.printStackTrace();
                }
            }
            else{
            }
        });
//        TemplateController.destroy("ARIA TC",db);

    }

    public Manager getManager(String name){
        for(Manager m : managers){
            if(m.getName().equals(name))
            {
                return m;
            }
        }
        return null;
    }

}
