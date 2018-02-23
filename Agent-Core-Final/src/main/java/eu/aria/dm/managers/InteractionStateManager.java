package eu.aria.dm.managers;

import eu.aria.util.activemq.SimpleProducerWrapper;
import eu.aria.util.activemq.util.UrlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by WaterschootJB on 29-5-2017.
 * This class keeps track of the interaction state of the dialogue (IDLE/ENGAGING/DISENGAGING/ENGAGED)
 * and of the internal interaction state of the agent (IDLE,WAIT,YIELD,LISTEN,TALK,INTERRUPT).
 */
public class InteractionStateManager extends SimpleManager {

    private static Logger logger = LoggerFactory.getLogger(InteractionStateManager.class.getName());
    private SimpleProducerWrapper interactionStateSender;
    private String port = "61616";
    private String host = "localhost";
    private String topic = "vib.input.aria.InformationStates";
    private boolean isTopic = true;

    public InteractionStateManager(){


    }

    /**
     * Start the ActiveMQ producer for sending the interaction states
     */
    public void startAMQProducer() {
        interactionStateSender = new SimpleProducerWrapper(UrlBuilder.getUrlTcp(host,port),topic,isTopic);
        interactionStateSender.init();
        logger.info("InteractionState Wrapper started");
    }

    /**
     * Call this function if you want to send an information state change to GRETA
     * @param interactionState
     */
    public String sendInteractionState(String interactionState){
        Message m = interactionStateSender.createTextMessage("");
        try {
            m.setStringProperty("interaction-state",interactionState);
            //m.setStringProperty("language", "english");
            interactionStateSender.sendMessage(m);
            logger.debug("TextMessage sent: " + interactionState);
            return interactionState;
        } catch (JMSException e) {
            logger.error("could not set property interaction-state to: " + interactionState);
            e.printStackTrace();
        }
        return "";
    }

}
