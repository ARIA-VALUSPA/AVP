package eu.aria.dm.behaviours;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

/**
 * Created by WaterschootJB on 4-7-2017.
 * Class for adapting the natural language, based on dialogue history for example
 */
public class AlignmentGeneration {

    private OkHttpClient client;
    private String coreUrl;
    private String host;
    private int port;
    private String headerVariable;
    private String headerValue;
    private static Logger logger = LoggerFactory.getLogger(AlignmentGeneration.class.getName());
    Properties prop = new Properties();
    InputStream input = null;

    /**
     * Constructor with custom header
     * @param host, the server running the VA adaptation
     * @param port, the port of the server
     * @param headerVariable, the header variable name
     * @param headerValue, the header variable value
     */
    public AlignmentGeneration(String host, int port, String headerVariable, String headerValue){
        client = new OkHttpClient();
        this.host = host;
        this.port = port;
        this.coreUrl = String.format("http://%s:%d/",host,port);
        this.headerVariable = headerVariable;
        this.headerValue = headerValue;
    }

    /**
     * Default constructor for initializing the alignment generation
     * @param host, the server's address
     * @param port, the port of the server
     */
    public AlignmentGeneration(String host, int port){
        this(host, port,"content-type","application/x-www-form-urlencoded");
    }

    /**
     * Default constructor for initializing the alignment generation with the nlg.properties
     */
    public AlignmentGeneration(){
        String filename = "nlg.properties";
        try {
            input = this.getClass().getClassLoader().getResourceAsStream(filename);
            if(input==null){
                logger.error("Unable to find " + filename);
                return;
            }
            prop.load(input);
            this.host = prop.getProperty("host");
            this.port = Integer.parseInt(prop.getProperty("port"));
            this.coreUrl = String.format("http://%s:%d/",host,port);
        }
        catch(IOException ex){
            ex.printStackTrace();
        } finally {
            if(input!=null)
                try{
                    input.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
        }
        client = new OkHttpClient();
        this.headerVariable = "content-type";
        this.headerValue = "application/x-www-form-urlencoded";

    }

    /**
     * Checks if the VA server is still active
     * @return if the server is active
     */
    public boolean active(String ID){
        try(Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(host,port),10000);
            logger.info("Alignment Module Active");
            if(this.getConversationHistory(ID).size() > 0){
                this.deleteConversation(ID);
            }
            return true;
        } catch(IOException e){
            logger.error("Timeout or unreachable or failed DNS lookup");
            return false;
        }
    }

    /**
     * Appending an utterance to the dialogue history
     * @param conversationID, a unique ID given to a conversation between agent and human
     * @param utterance, an utterance said by the agent/user
     * @param speaker, agent or user
     * @return true if the append was successful
     */
    public boolean appendToHistory(String conversationID, String utterance, String speaker){

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("conversation_id",conversationID);
        formBuilder.add("utterance",utterance);
        formBuilder.add("speaker",speaker);

        Response response = null;

        Request request = new Request.Builder()
                .url(coreUrl + "append_to_history")
                .post(formBuilder.build())
                .addHeader(headerVariable,headerValue)
                .build();

        try{
            response = client.newCall(request).execute();
            if(response.isSuccessful()){
                logger.info("Added in conversation '{}' from speaker '{}' the utterance '{}'",conversationID,speaker,utterance);
                response.close();
                return true;
            }
            response.close();
        } catch (IOException e) {
            logger.error("Could not add '{}' to conversation '{}' for speaker '{}'!",utterance,conversationID,speaker);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Retrieve all modifications for a conversation
     * @param conversationID, the unique ID for the conversation you want to use the history from
     * @param agentUtterance, the agent utterance you want to adjust
     * @return an array of possible agent utterances
     */

    public JsonArray getAllModifications(String conversationID, String agentUtterance){
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("conversation_id",conversationID);
        formBuilder.add("agent_response",agentUtterance);

        Request request = new Request.Builder()
                .url(coreUrl + "get_all_modifications")
                .post(formBuilder.build())
                .addHeader(headerVariable,headerValue)
                .build();
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                JsonReader reader = Json.createReader(new StringReader(response.body().string()));
                logger.info("Retrieved modifications from conversation '{}' for utterance '{}'",conversationID,agentUtterance);
                response.close();
                return reader.readArray();
            }
            response.close();
        } catch (IOException e) {
            logger.error("Could not get all modifications from '{}' for agent utterance '{}'",conversationID,agentUtterance);
            e.printStackTrace();
        }
        return Json.createArrayBuilder().build();
    }

    public String getAllModificationsString(String conversationId, String agentUtterance){
        JsonArray currentHistory = this.getConversationHistory(conversationId);
        System.out.println(currentHistory);
        JsonArray ja = getAllModifications(conversationId,agentUtterance);
        String modifications = ja.toString();
        System.out.println("Mods: " + modifications);
        return modifications;
    }

    /**
     * Retrieve only convergent modified answers (faster than retrieving them all)
     * @param conversationID, the unique ID for the conversation you want to use history from
     * @param agentUtterance, the agent utterance you want to adjust
     * @return all convergent modified answers
     */
    public JsonArray getAllConvergent(String conversationID, String agentUtterance){
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("conversation_id",conversationID);
        formBuilder.add("agent_response",agentUtterance);

        Request request = new Request.Builder()
                .url(coreUrl + "get_all_convergent")
                .post(formBuilder.build())
                .addHeader(headerVariable,headerValue)
                .build();
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                JsonReader reader = Json.createReader(new StringReader(response.body().string()));
                logger.info("Retrieved convergent from conversation '{}' and agent utterance '{}'",conversationID,agentUtterance);
                response.close();
                return reader.readArray();
            }
            response.close();
        } catch (IOException e) {
            logger.error("Could not retrieve list of convergent answers from conversation '{}' and utterance '{}'",conversationID,agentUtterance);
            e.printStackTrace();
        }
        return Json.createArrayBuilder().build();
    }

    /**
     * Retrieve only divergent modified answers
     * @param conversationID, unique conversation ID
     * @param agentUtterance, the agent utterance to be modified
     * @return the divergent modified answers
     */
    public JsonArray getAllDivergent(String conversationID, String agentUtterance){
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("conversation_id",conversationID);
        formBuilder.add("agent_response",agentUtterance);

        Request request = new Request.Builder()
                .url(coreUrl+ "get_all_divergent")
                .post(formBuilder.build())
                .addHeader(headerVariable,headerValue)
                .build();
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                JsonReader reader = Json.createReader(new StringReader(response.body().string()));
                logger.info("Retrieved all divergent modified answers for conversation '{}' and utterance '{}'",conversationID,agentUtterance);
                response.close();
                return reader.readArray();
            }
        } catch (IOException e) {
            logger.error("Could not retrieve all divergent modified answers of conversation '{}' for utterance '{}'",conversationID,agentUtterance);
            e.printStackTrace();
        }
        return Json.createArrayBuilder().build();
    }

    /**
     * Get a modification for a single user utterance without any history
     * @param userUtterance, the user utterance
     * @param agentUtterance, the to be modified agent utterance
     * @return the modified sentences for the agent
     */
    public JsonArray getModificationsNoHistory(String userUtterance, String agentUtterance){
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("user_utterance",userUtterance);
        formBuilder.add("agent_response",agentUtterance);

        Request request = new Request.Builder()
                .url(coreUrl + "get_modifications_nohistory")
                .post(formBuilder.build())
                .addHeader(headerVariable,headerValue)
                .build();
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                JsonReader reader = Json.createReader(new StringReader(response.body().string()));
                logger.info("Retrieved all modifications for user saying '{}' and agent utterance '{}'",userUtterance,agentUtterance);
                response.close();
                return reader.readArray();
            }
            response.close();
        } catch (IOException e) {
            System.err.println("Uh-oh, something went terribly wrong!");
            logger.error("Could not retrieve all modified answers for user utterance '{}' and agent utterance '{}'",userUtterance,agentUtterance);
            e.printStackTrace();
        }
        return Json.createArrayBuilder().build();
    }

    /**
     * Get all the conversations currently on the server
     * @return a JsonArray, where each element is the ID of a conversation currently in memory
     */
    public JsonArray getConversationList(){

        Request request = new Request.Builder()
                .url(coreUrl +"get_conversation_list")
                .post(RequestBody.create(null, ""))
                .addHeader(headerVariable,headerValue)
                .build();
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                JsonReader reader = Json.createReader(new StringReader(response.body().string()));
                logger.info("Retrieved all conversation history from the server");
                response.close();
                return reader.readArray();
            }
            response.close();
        } catch (IOException e) {
            logger.error("Could not retrieve the full conversation history!");
            e.printStackTrace();
        }
        return Json.createArrayBuilder().build();
    }

    /**
     * Delete all conversation history
     * @return true if the operation was successful
     */
    public boolean deleteAllConversationHistory(){

        Request request = new Request.Builder()
                .url(coreUrl + "delete_conversation_history")
                .post(RequestBody.create(null, ""))
                .addHeader(headerVariable,headerValue)
                .build();
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                logger.info("Deleted entire history");
                response.close();
                return true;
            }
            response.close();
        } catch (IOException e) {
            logger.error("Could not delete entire conversation history");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a conversation with this particular ID
     * @return true if the operation was successful
     */
    public boolean deleteConversation(String conversationID){

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("conversation_id",conversationID);

        Request request = new Request.Builder()
                .url(coreUrl + "delete_conversation")
                .post(formBuilder.build())
                .addHeader(headerVariable,headerValue)
                .build();
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                logger.info("Deleted conversation '{}'",conversationID);
                response.close();
                return true;
            }
            response.close();
        } catch (IOException e) {
            logger.error("Could not delete conversation '{}'",conversationID);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieve the whole history of a conversation a conversation with this particular ID
     * @return an array of strings containing the speaker/utterance turns
     */
    public JsonArray getConversationHistory(String conversationID){

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("conversation_id",conversationID);

        Request request = new Request.Builder()
                .url(coreUrl + "get_conversation_history")
                .post(formBuilder.build())
                .addHeader(headerVariable,headerValue)
                .build();
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                JsonReader reader = Json.createReader(new StringReader(response.body().string()));
                logger.info("Retrieved the conversation history for conversation '{}'.",conversationID);
                response.close();
                return reader.readArray();
            }
            response.close();
        } catch (IOException e) {
            logger.error("Could not retrieve the conversation history for conversation '{}'.",conversationID);
            e.printStackTrace();
        }
        return Json.createArrayBuilder().build();
    }


}
