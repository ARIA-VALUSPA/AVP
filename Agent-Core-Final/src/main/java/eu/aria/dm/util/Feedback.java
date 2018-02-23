package eu.aria.dm.util;

import eu.aria.dm.managers.GeneralManager;
import eu.aria.dm.managers.NLUManager;
import eu.aria.util.activemq.SimpleProducerWrapper;
import eu.aria.util.activemq.util.UrlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.TextMessage;
import javax.json.*;
import java.io.StringReader;

/**
 * Created by WaterschootJB on 3-6-2017.
 */
public class Feedback {

    private static Logger logger = LoggerFactory.getLogger(NLUManager.class.getName());
    private static SimpleProducerWrapper dialogEventProducer;
    private String topic = "dialog";
    private String port = "61616";
    private String host = "localhost";
    private boolean isTopic = true;

    public Feedback(){
        dialogEventProducer = new SimpleProducerWrapper(UrlBuilder.getUrlTcp(host,port),topic,isTopic);
        dialogEventProducer.init();
    }

    public static void sendTemplateID(String ID){
        TextMessage textMessage = dialogEventProducer.createTextMessage(ID);
        dialogEventProducer.sendMessage(textMessage);
        logger.info("Template ID: {}\n",ID);
    }

    public static void printIS(String IS){
        JsonReader jr = Json.createReader(new StringReader(IS));
        JsonStructure js = jr.read();
        if(js.getValueType().equals(JsonObject.ValueType.OBJECT)){
            JsonObject jo = (JsonObject) js;
            logger.info(jo.toString());
        }
        else{
            JsonArray ja = (JsonArray) js;
            logger.info(ja.toString());

        }



    }

    public static void printIS(Integer IS){
        logger.debug(Integer.toString(IS));

    }

}
