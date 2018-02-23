package eu.aria.dm.managers;

import eu.aria.util.activemq.SimpleReceiverWrapper;
import eu.aria.util.activemq.util.UrlBuilder;
import hmi.flipper2.FlipperException;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;


/**
 * Created by WaterschootJB on 19-5-2017.
 */
public class InputManager extends SimpleManager{

    protected BlockingQueue<String> queue = null;
    private static Logger logger = LoggerFactory.getLogger(InputManager.class.getName());

    private String xmldata = "";
    private SimpleReceiverWrapper receiverSSI;
    private SimpleReceiverWrapper receiverASR;
    private String port = "61616";
    private String host = "localhost";
    private boolean isTopic = true;
    private DocumentBuilder docBuilder;
    private XPath xpath;
    private String previousJSON;

    /**
     * Construct default InputManager
     * @throws FlipperException
     */
    public InputManager() throws FlipperException, ParserConfigurationException {
        super();
        this.queue = new LinkedBlockingQueue<>();
        String url = UrlBuilder.getUrlTcp(host,port);
        receiverSSI = new SimpleReceiverWrapper(url,"SSI",isTopic);
        receiverASR = new SimpleReceiverWrapper(url,"ASR",isTopic);
        this.process();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        XPathFactory xPathFactory = XPathFactory.newInstance();
        xpath = xPathFactory.newXPath();
        docBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    public boolean hasMessage(){
        return !queue.isEmpty();
    }

    public boolean isConnected(){
        return (receiverSSI.isReceiver() && receiverASR.isReceiver());
    }

    public void receiveData(String data){
        logger.debug("User data received");
        //Ugly method to clear the queue if it becomes too large
        if(queue.size() > 1){
            queue.clear();
        }
        queue.add(data);
    }

    /**
     * Function for updating the IS with user sensory input read from the SSITemplate.xml format
     * @return the updated information state concerning the user states
     */
    public String updateIS(){
        try{
            logger.debug("Updating IS");
            String message = queue.take();
            logger.debug("Messages in queue: " + queue.size());
            String type = message.trim().substring(0,1);
            //Case XML
            if(type.equals("<")){
                //Replacing the <UNK>, <NOISE> tags, etc to prevent XML parsing errors
                message = message.replaceAll(Pattern.quote("<UNK>"),"[UNK]");
                message = message.replaceAll(Pattern.quote("<NOISE>"),"[NOISE]");
                message = message.replaceAll(Pattern.quote("<LAUGHTER>"),"[LAUGHTER]");

                //Retrieving the ASR data (in JSON format) from within XML
                try{
                    Document doc = docBuilder.parse(new InputSource(new StringReader(message)));
                    String json =XML.toJSONObject(message,true).toString(4);
                    previousJSON = json;
                    return json;
                }
                catch(Exception e){
                    logger.error("Failed parsing JSON to XML");
                    return previousJSON;
                }
            }
            //Case JSON
            else if(type.equals("{")){
                return message;
            }
            //Case not JSON or XML
            else{
                logger.error("Input message string of unknown type (not XML/JSON)");
                return "{}";
            }
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        return "{}";

    }

    /**
     * Function for starting up the AMQ message receiver and putting it into an XMLString
     */
    @Override
    public void process(){
        logger.info("ASR Wrapper started");
        receiverASR.start((Message message) ->{
            if(message instanceof TextMessage){
                try{
                    xmldata = (((TextMessage) message).getText());
                    receiveData(xmldata);
                }
                catch(JMSException e){
                }
            }
            else{
            }
        });
        logger.info("SSI Wrapper started");
        receiverSSI.start((Message message) ->{
            if(message instanceof TextMessage){
                try{
                    xmldata = (((TextMessage) message).getText());
                    receiveData(xmldata);
                }
                catch(JMSException e){
                }
            }
            else{
            }
        });
    }

    public static void Log(String s) {
        logger.debug("\n===\n{}\n===", s);
    }


}
