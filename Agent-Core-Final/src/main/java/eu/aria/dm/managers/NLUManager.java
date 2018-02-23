package eu.aria.dm.managers;

//import com.github.vbauer.yta.model.Translation;
//import com.github.vbauer.yta.service.YTranslateApiImpl;
//import com.github.vbauer.yta.model.Language;
import eu.aria.dm.util.Say;
import eu.aria.dm.util.SentencesToKeywords;
import eu.aria.util.activemq.SimpleProducerWrapper;
import eu.aria.util.activemq.util.UrlBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qamatcher.Dialog;
import qamatcher.TestQAResponder;

import javax.jms.Message;
import javax.json.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * Created by WaterschootJB on 28-5-2017.
 * This Module should interpret the ASR speech and do some magic with it
 */
public class NLUManager extends SimpleManager{

    private TestQAResponder qamatcher;
    private SentencesToKeywords sk;
    private String previousText = "";
    private Say userSay;
    private boolean question = true;
    private Logger logger = LoggerFactory.getLogger(NLUManager.class.getName());
//    private YTranslateApiImpl api;
    private boolean active;
    private SimpleProducerWrapper sendUserData;
    private String amqHostname;
    private String amqPort;
    private String topic;

    private String language;
    private int nbest;
    private String mode;
    private double threshold;

    private String logfile;


    /**
     * Constructur for using custom files for your stopwords, synonyms, POS model and qa-pairs
     * @param stopwords
     * @param synonyms
     * @param posModel
     * @param questionanswers
     */
    public NLUManager(String stopwords, String synonyms, String posModel, String questionanswers){
        super();
        try {
            sk = new SentencesToKeywords(stopwords, synonyms, posModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        qamatcher = new TestQAResponder(questionanswers,getClass().getClassLoader().getResource("data/defaultanswers.txt").getPath());
        userSay = null;
        active = true;
        this.threshold = 0.2;
        this.amqHostname = "localhost";
        this.amqPort = "61616";
        this.topic = "dialog";
        this.sendUserData = new SimpleProducerWrapper(UrlBuilder.getUrlTcp(amqHostname,amqPort),topic,true);
        this.sendUserData.init();

    }

    /**
     * Default constructor for NLUManager by loading the resources.
     */
    public NLUManager(){
        super();
        String stopwords = getClass().getClassLoader().getResource("data/stopwords.txt").getPath();
        String synonyms = getClass().getClassLoader().getResource("data/synonyms.txt").getPath();
        String posmodel = getClass().getClassLoader().getResource("data/english-left3words-distsim.tagger").getPath();
        String resourceQAMfile = "data/QAM_AliceEvaluationQuest1.xml";
        String resourceQAMdefaultanswersfile = "data/defaultanswers.txt";

        try {
            FileReader fileReader = new FileReader(getClass().getClassLoader().getResource("ariaQAM.properties").getPath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("resourceQAMfile:")){
                    resourceQAMfile = line.replace("resourceQAMfile:","").trim();
                    System.out.println("QAM config: "+line);
                }
                if (line.startsWith("resourceQAMdefaultanswersfile:")){
                    resourceQAMdefaultanswersfile = line.replace("resourceQAMdefaultanswersfile:","").trim();
                    System.out.println("QAM config: "+line);
                }
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            sk = new SentencesToKeywords(stopwords, synonyms,posmodel);

        } catch (IOException e) {
            e.printStackTrace();
        }
        qamatcher = new TestQAResponder(getClass().getClassLoader().getResource(resourceQAMfile).getPath(),getClass().getClassLoader().getResource(resourceQAMdefaultanswersfile).getPath());
        userSay = null;
        //String key = "trnsl.1.1.20170729T135337Z.408ee7bb3dbaadd4.d17a4c0da785e16bcec4028829aed0d189107704";
        //api = new YTranslateApiImpl(key);
        active = true;
        logger.info("NLU Manager active");
        this.threshold = 0.2;
        this.amqHostname = "localhost";
        this.amqPort = "61616";
        this.topic = "dialog";
        this.sendUserData = new SimpleProducerWrapper(UrlBuilder.getUrlTcp(amqHostname,amqPort),topic,true);
        this.sendUserData.init();
    }

    public boolean active(){
        return this.active;
    }

    public boolean active(String language, String mode, String nbest){
        this.language = language;
        this.mode = mode;
        this.nbest = Integer.parseInt(nbest);
        return this.active;
    }

    /**
     * Creates the dialogue history
     * @return filename
     */
    public String createFile(){
        String header = "ID;START;END;ACTOR;SPEECH\n";
        Charset charset = Charset.forName("UTF-8");
        byte data[] = header.getBytes(charset);
        DateTimeFormatter formatToday = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime today = LocalDateTime.now();
        logfile = "./log/" + today.format(formatToday) + "-conversation.csv";
        Path file = Paths.get(logfile);
        File f = new File("./log");
        f.mkdirs();
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(file, CREATE, APPEND))) {
            out.write(data, 0, data.length);
        } catch (IOException e) {
            logger.error("IOException: %s%n",e);
        }
        return logfile;
    }

    /**
     * Retrieves the list of moves and their corresponding relevance
     * @param lastMove the last move performed by the agent
     * @param movesArray list of possible moves
     * @return a list of moves with their corresponding structure score
     */
    public String updateRelevance(String lastMove, String movesArray){
        JsonReader readMoves = Json.createReader(new StringReader(movesArray));
        JsonReader readPrevious = Json.createReader(new StringReader(lastMove));
        JsonArray currentMoves = readMoves.readObject().getJsonArray("possibleMoves");
        JsonObject previous = readPrevious.readObject();

        String[] structure = previous.getString("previousMove").split("_");
        String previousEpisode = structure[0];
        String previousExchange = structure[1];
        String previousMove = structure[2];

        JsonArrayBuilder jab = Json.createArrayBuilder();

        for(int i=0; i < currentMoves.size(); i++){
            JsonObject currentMove = currentMoves.getJsonObject(i);
            double currentScore = currentMove.getJsonNumber("relevance").doubleValue();
            String[] moveStructure = currentMove.getJsonString("id").getString().split("_");
            if(moveStructure[0].equals(previousEpisode)){
                currentScore += 0.01;
            }
            if(moveStructure[1].equals(previousExchange)){
                currentScore += 0.1;
            }
            if(moveStructure[2].equals(previousMove)){
                currentScore -= 0.5;
            }
            if(currentScore > 1){
                currentScore = 1;
            }
            if(currentScore < 0){
                currentScore = 0;
            }
            jab.add(Json.createObjectBuilder().add("id",currentMove.getJsonString("id")).add("relevance",currentScore));
        }
        JsonArray structureScores = jab.build();

        return structureScores.toString();
    }

    /**
     * Method for initializing the relevance scores and storing them in the information state
     */
    public String initializeMoves() {
        List <Pair<Dialog,Double>> movesAndRelevance = qamatcher.getStore().retrieveQueries("");
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for(Pair<Dialog,Double> p : movesAndRelevance){
            int randomNum = ThreadLocalRandom.current().nextInt(0,p.getKey().answerSize());
            //System.out.println("query: " + p.getKey().getAnswer(randomNum).answer + " score: " + p.getValue());
            builder.add(Json.createObjectBuilder()
                    .add("id",p.getKey().getAnswer(randomNum).answer)
                    .add("relevance",p.getValue()));
        }
        JsonArray ja = builder.build();
        return ja.toString();
    }

    /**
     * Calculates the QAM score for all moves when new speech is detected
     * @param userUtterance, a JSON representation of ASR_output, namely:
     * {
     *     IDUR
     *     LANGUAGE
     *     NBEST
     *     MODE
     *     PARTIAL
     *     RDUR
     *     TRANSCRIPTIONS{
     *         NWORDS
     *         TEXT
     *         ID
     *     }
     * }     *
     * @return a JSON Array of [moveName(ID),qascore]
     */
    public String calculateMovesQAMScores(String userUtterance){
        JsonReader reader = Json.createReader(new StringReader(userUtterance));
        JsonObject ASR_output = reader.readObject();
        Object transcriptions = ASR_output.get("transcriptions");
        String language = ASR_output.getJsonString("language").getString();
        long timestamp = System.currentTimeMillis();
        String durationS = ASR_output.getJsonString("idur").getString();
        Double durationD = Double.valueOf(durationS)*1000;
        long duration = durationD.longValue();
        String actorName = "User";


        if(this.mode.equals("utt")){
            userSay = new Say();
            userSay.setTimestamp(timestamp);
            userSay.setActorName(actorName);
            userSay.setLanguage(language);
            userSay.setTyped(false);

            //If array
            if(transcriptions instanceof JsonArray){
                JsonArray mTranscripts = ASR_output.getJsonArray("transcriptions");
                //Need to be looped instead of just picking first one like this:
                JsonObject transcript = mTranscripts.getJsonObject(0);
                String text = transcript.getString("text").toLowerCase();
                if(previousText.equals(text))
                    return "[]";
                previousText = text;
                System.out.println("ASR: "+text);
                this.userSay.setLength(duration);
                this.userSay.setText(text);
                this.userSay.setTalking(false);
                this.addUserSay();

                List <Pair<Dialog,Double>> movesAndRelevance = qamatcher.getStore().retrieveQueries(text);
//                System.out.println("size moves: "+movesAndRelevance.size());

                JsonArrayBuilder builder = Json.createArrayBuilder();
                for(Pair<Dialog,Double> p : movesAndRelevance){
                    int randomNum = ThreadLocalRandom.current().nextInt(0,p.getKey().answerSize());
                    //System.out.println("query: " + p.getKey().getAnswer(randomNum).answer + " score: " + p.getValue());
                    builder.add(Json.createObjectBuilder()
                            .add("id",p.getKey().getAnswer(randomNum).answer)
                            .add("relevance",p.getValue()));
                }
                JsonArray ja = builder.build();
//                System.out.println("2");
                return ja.toString();
            }
            // If n == 1
            else{
                JsonObject transcript = ASR_output.getJsonObject("transcriptions");
                String text = transcript.getString("text").toLowerCase();
                if(previousText.equals(text))
                    return "[]";
                previousText = text;
                System.out.println("ASR: "+text);
                this.userSay.setLength(duration);
                this.userSay.setText(text);
                this.userSay.setTalking(false);
                this.addUserSay();
                List <Pair<Dialog,Double>> movesAndRelevance = qamatcher.getStore().retrieveQueries(text);
//                System.out.println("size moves: "+movesAndRelevance.size());

                JsonArrayBuilder builder = Json.createArrayBuilder();
                for(Pair<Dialog,Double> p : movesAndRelevance){
                    int randomNum = ThreadLocalRandom.current().nextInt(0,p.getKey().answerSize());
                    //System.out.println("query: " + p.getKey().getAnswer(randomNum).answer + " score: " + p.getValue());
                    builder.add(Json.createObjectBuilder()
                            .add("id",p.getKey().getAnswer(randomNum).answer)
                            .add("relevance",p.getValue()));
                }
                JsonArray ja = builder.build();
//                System.out.println("2");
                return ja.toString();

            }
        }
        if(this.mode.equals("inc")){
            //Partial output is always with n == 1
            if(ASR_output.getString("partial").equals("True")){
                String partial_text = ASR_output.getJsonObject("transcriptions").getString("text").toLowerCase();
                if(userSay == null)
                    this.userSay = new Say();
                this.userSay.setText(partial_text);
                this.userSay.setTalking(true);

                System.out.println("ASR part: "+partial_text);
                //TODO: do some planning anticipation planMove or interaction moves;
                return "[]";
            }
            else {
                if(userSay == null)
                    this.userSay = new Say();
                userSay.setTimestamp(timestamp);
                userSay.setActorName(actorName);
                userSay.setLanguage(language);
                userSay.setTyped(false);
                //Check if array
                if(transcriptions instanceof JsonArray){
                    JsonArray mTranscripts = ASR_output.getJsonArray("transcriptions");
                    //Need to be looped instead of just picking first one like this:
                    JsonObject transcript = mTranscripts.getJsonObject(0);
                    String text = transcript.getString("text").toLowerCase();
                    if (previousText.equals(text))
                        return "[]";
                    previousText = text;
                    System.out.println("ASR: " + text);

                    this.userSay.setText(text);
                    this.userSay.setLength(duration);
                    this.userSay.setTalking(false);
                    this.addUserSay();

                    List<Pair<Dialog, Double>> movesAndRelevance = qamatcher.getStore().retrieveQueries(text);
    //              System.out.println("size moves: "+movesAndRelevance.size());

                    //Create the list of moves and their QAM scores and put it into a JsonString
                    JsonArrayBuilder builder = Json.createArrayBuilder();
                    for (Pair<Dialog, Double> p : movesAndRelevance) {
                        int randomNum = ThreadLocalRandom.current().nextInt(0, p.getKey().answerSize());
                        //System.out.println("query: " + p.getKey().getAnswer(randomNum).answer + " score: " + p.getValue());
                        builder.add(Json.createObjectBuilder()
                                .add("id", p.getKey().getAnswer(randomNum).answer)
                                .add("relevance", p.getValue()));
                    }
                    JsonArray ja = builder.build();

                    return ja.toString();
                }
                //If n == 1
                else{
                    JsonObject transcript = ASR_output.getJsonObject("transcriptions");
                    String text = transcript.getString("text").toLowerCase();
                    if (previousText.equals(text))
                        return "[]";
                    previousText = text;
                    System.out.println("ASR: " + text);

                    this.userSay.setText(text);
                    this.userSay.setLength(duration);
                    this.userSay.setTalking(false);
                    this.addUserSay();

                    List<Pair<Dialog, Double>> movesAndRelevance = qamatcher.getStore().retrieveQueries(text);
                    //              System.out.println("size moves: "+movesAndRelevance.size());

                    //Create the list of moves and their QAM scores and put it into a JsonString
                    JsonArrayBuilder builder = Json.createArrayBuilder();
                    for (Pair<Dialog, Double> p : movesAndRelevance) {
                        int randomNum = ThreadLocalRandom.current().nextInt(0, p.getKey().answerSize());
                        //System.out.println("query: " + p.getKey().getAnswer(randomNum).answer + " score: " + p.getValue());
                        builder.add(Json.createObjectBuilder()
                                .add("id", p.getKey().getAnswer(randomNum).answer)
                                .add("relevance", p.getValue()));
                    }
                    JsonArray ja = builder.build();

                    return ja.toString();

                }
            }
        }
        System.out.println("Incorrect format of ASR input, is neither 'inc' or 'utt'" + userUtterance);
        return "[]";
    }

    public boolean trustLevelIs(String currentAgentEmotion, String condition){
        float curTrust = 0;
        try {
            JsonObject curEmo = Json.createReader(new StringReader(currentAgentEmotion)).readObject();
            curTrust = Float.parseFloat(curEmo.getString("trust"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("curTrust: "+curTrust);
            if (curTrust > 0.8) {
                System.out.println("trusting!");
                if(condition.equals("trust")){
                    return true;
                }
                return false;
            }
        }catch (Exception e){
        }
        System.out.println("distrusting");
        if(condition.equals("trust")){
            return false;
        }
        return true;
    }

    private void createMove(JsonArray transcriptions) {
    }

    public void addUserSay() {
        if(logfile != null){
            long endTime = userSay.getTimestamp() + userSay.getLength();
            String turn = userSay.getId() + ";" + userSay.getTimestamp() + ";" + endTime + ";" + userSay.getActorName() + ";" + userSay.getText();
            Charset charset = Charset.forName("UTF-8");
            byte data[] = (turn + System.lineSeparator()).getBytes(charset);
            Path file = Paths.get(logfile);
            try (OutputStream out = new BufferedOutputStream(
                    Files.newOutputStream(file, CREATE, APPEND))) {
                out.write(data, 0, data.length);
                Message m = this.sendUserData.createTextMessage(turn);
                this.sendUserData.sendMessage(m);
            } catch (IOException e) {
                logger.error("IOException: %s%n",e);
            }
            //this.userSay = null;
        }
    }

    public String getLastText(){
        if(userSay == null || userSay.getText() == null){
            return "";
        }
        return this.userSay.getText();
    }


//    public String process(String userSay){
//
//        if(userSay == null){
//            logger.info("Empty userSay");
//            return "";
//        }
//        userSay = userSay.toLowerCase();
//        System.out.println("\nIt processed in NLU");
//        String reply = "";
//        if(question){
//            String QAMquery = qamatcher.findMatchingQuery(userSay, "type", "certain");
//            String QAManswer = qamatcher.findAndReturn(userSay, null, null);
//
//            //For some reason newlines are in the string returned by the QAMatcher
//            reply = QAManswer.replaceAll("\\r\\n|\\r|\\n", "");
//        }
//        else{
//            userSay = userSay.replaceAll("\\?", " ?");
//            userSay = userSay.replaceAll("\\.", " .");
//            ArrayList<String> userSayAL = sk.removeStopWords(userSay); //ugly quick hack b/c pickup is by ref
//            String tokenize = userSay.trim().replaceAll("\\s+", " ");
//            tokenize = tokenize.replaceAll("\\,", " ,");
//            String[] words = tokenize.split(" ");
//            ArrayList<String> hasNN = new ArrayList<>(java.util.Arrays.asList(words));
//            try {
//                //Try to see if the next question is a follow-up question
//                String isFollowup = sk.isFollowUp(hasNN)?"true":"false";
//                userSayAL = sk.pickUp(userSayAL);
//            } catch (IOException ex) {
//                logger.error("Something went wrong with selecting keywords in {}",NLUManager.class.getName());
//            }
//            //We create a list with the most important words
//            JsonArray keywords = Json.createArrayBuilder().build();
//            for(String word : userSayAL){
//                keywords.add((JsonValue) Json.createObjectBuilder().add("words",word));
//            }
//        }
//        return reply;
//    }

//    public String translate(String utterance, String toLanguage){
//
//        Language l = Language.of(toLanguage);
//        Optional<Language> language = api.detectionApi().detect(utterance);
//        if(!language.equals(l)){
//            //System.out.println("Language: " + language.get());
//            Translation translatedText = api.translationApi().translate(utterance, l);
//            //System.out.println("Translation: " + translatedText.text());
//            //System.out.println("Direction: " + "From: " + translatedText.direction().source().get() + " to "+ translatedText.direction().target());
//
//            JsonObject translation = (JsonObject) Json.createObjectBuilder()
//                    .add("Translation", Json.createObjectBuilder()
//                            .add("Original Language",language.get().toString())
//                            .add("Target Language",l.toString())
//                            .add("Translation",translatedText.text())
//                            .build());
//            return translation.toString();
//        }
//        else{
//            return utterance;
//        }
//
//    }




}
