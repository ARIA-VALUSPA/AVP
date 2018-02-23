package eu.aria.dm;

import eu.aria.dm.behaviours.AlignmentGeneration;
import eu.aria.dm.util.Metaphone3;
import eu.aria.dm.util.ModifiedSentence;
import org.apache.commons.lang3.tuple.Pair;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

import javax.json.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

public class Test {

    public static void main(String[] args) throws IOException {
        //testAll();
        //testExperiment();
        //testJsonMapper();
        //String coreURL = "http://zilver015117.mobiel.utwente.nl:8080/";
        //String agentUtterance = "I picked up the white kid gloves and ran after the white rabbit.";
        //AlignmentGeneration adaptation = new AlignmentGeneration();
        //JsonArray ja = adaptation.getConversationHistory("default");
        //System.out.println("Conversation retrieved");
        testSimilarity();
    }

    private static void testSimilarity(){

        //Create list of all similarity measures
        ArrayList<StringMetric> allMetrics = new ArrayList();
        allMetrics.add(StringMetrics.simonWhite());
        allMetrics.add(StringMetrics.cosineSimilarity());
        allMetrics.add(StringMetrics.damerauLevenshtein());
        allMetrics.add(StringMetrics.dice());
        allMetrics.add(StringMetrics.overlapCoefficient());
        allMetrics.add(StringMetrics.qGramsDistance());
        allMetrics.add(StringMetrics.levenshtein());
        allMetrics.add(StringMetrics.blockDistance());
        allMetrics.add(StringMetrics.euclideanDistance());
        allMetrics.add(StringMetrics.generalizedJaccard());
        allMetrics.add(StringMetrics.jaro());
        allMetrics.add(StringMetrics.identity());
        allMetrics.add(StringMetrics.jaccard());
        allMetrics.add(StringMetrics.jaroWinkler());
        allMetrics.add(StringMetrics.mongeElkan());
        allMetrics.add(StringMetrics.needlemanWunch());
        allMetrics.add(StringMetrics.smithWaterman());
        allMetrics.add(StringMetrics.smithWatermanGotoh());

        //Add test questions here in pairs, with left = actual sentence, right = ASR transcription
        ArrayList<Pair> strings = new ArrayList();
        strings.add(Pair.of("i'd love to hear what you have to say about wonderland","i love to hear we have to say about wonder lance"));
        strings.add(Pair.of("did you chase the white rabbit","he chased a white rabbits"));
        strings.add(Pair.of("can you tell a joke","here selah joke"));
        strings.add(Pair.of("can you tell a joke","a fellow joke"));
        strings.add(Pair.of("can you talk a little slower please","k took her little with slower please"));
        strings.add(Pair.of("i spilled beer on cotton color","i feel beer on color wool"));
        strings.add(Pair.of("i spilled beer on cotton color","i spill beer on cotton color"));
        strings.add(Pair.of("how are you", "how graceful"));
        strings.add(Pair.of("who is the mad hatter", "who is the math hazur"));
        strings.add(Pair.of("what else can you do","what else k a"));
        strings.add(Pair.of("what did you do with the tiny golden key", "what is it with tiny golden key"));
        strings.add(Pair.of("did you eat the cake","you to cake"));
        strings.add(Pair.of("why was the mouse angry with you","why was the most angry were hugh"));
        strings.add(Pair.of("did you chase after the white rabbit","the chase of the de white rabbits"));

        //Set length of encoding
        int keylength = 10;

        for(Pair<String,String> p : strings){
            System.out.printf("Sentence a: %s \nSentence b: %s\n",p.getLeft(),p.getRight());

            Metaphone3 mpa = new Metaphone3();
            mpa.SetWord(p.getLeft());
            mpa.SetKeyLength(keylength);
            mpa.Encode();
            String code1 = mpa.GetMetaph();

            Metaphone3 mpb = new Metaphone3();
            mpb.SetWord(p.getRight());
            mpb.SetKeyLength(keylength);
            mpb.Encode();
            String code2 = mpb.GetMetaph();

            System.out.printf("Encoding a: %s \nEncoding b: %s\n",code1,code2);

            for(StringMetric metric : allMetrics){
                System.out.printf("Score (unencoded): %f and metric: %s\n",metric.compare(p.getLeft(),p.getRight()),metric.toString());
                System.out.printf("Score (encoded): %f and metric: %s\n",metric.compare(code1,code2),metric.toString());
            }
        }





    }


    private static void test(){
        //Initialize the VA module
        AlignmentGeneration adaptation = new AlignmentGeneration();
        adaptation.deleteConversation("ARIA");

        String agentUtterance1 = "I followed the rabbit into a hole and then <tm id=\"DMImpBegin\"/> fell down a very deep well! <tm id=\"DMImpEnd\"/> I came down upon a heap of sticks and dry leaves and found myself in a long, low hall. There, on a stool, was a tiny golden key!";
        String userUtterance = "What was the key for?";
        String agentUtterance2 ="I looked around and I found a little door. The key fitted in the lock, but <tm id=\"DMImpBegin\"/>  the door was too small for me to pass! <tm id=\"DMImpEnd\"/>  I looked around again and found a bottle with \"drink me\" written on it.";

        adaptation.appendToHistory("ARIA",agentUtterance1, "agent");
        adaptation.appendToHistory("ARIA",userUtterance,"user");


        JsonArray conversationHistory = adaptation.getConversationHistory("ARIA");
        for(JsonValue turn : conversationHistory){
            System.out.println(turn);
        }

        JsonArray modifications = adaptation.getAllModifications("ARIA",agentUtterance2);
        for(JsonObject m : modifications.getValuesAs(JsonObject.class)){
            ModifiedSentence ms = new ModifiedSentence(m);
            System.out.println(ms.toString());
        }



        //adaptation.deleteConversation("default");
    }

    private static void testExperiment(){
        AlignmentGeneration adaptation = new AlignmentGeneration();
        adaptation.deleteConversation("ASNLG");
        //String user =  "Ok. Uhm tell me about the orange Marmelade.";
        //String agent =  "It is against the laws of physics to drop a jar of marmelade in free fall.";

        //String user = "What does the white rabbit have?";
        //String agent = "The white rabbit has a pocket watch";

        //String user = "Uh there is a movie about the book, right?";
        //String agent = "Yes, Walt Disney made a movie in 1956";

        //String user = "can you tell me something about the character, the white rabbit?";
        //String agent = "The rabbit is being chased by Alice";

        //String user = "I would like to know about the cat.";
        //String agent = "Dinah is based on the actual cat owned by Alice Liddell.";

        //String user = "Why does the Queen want to convict Alice?";
        //String agent = "The Cheshire cat plays tricks with the queen. The queen blamed Alice for doing so.";

        //String agent3 = "Is there something else you'd like to know?";
        //String user = "Let me see...";
        //String agent2 ="Is there something...";
        //String user2 = "Yeah, yeah. Maybe... Something about the orange marmelade?";
        //String agent = "The falling jar of marmelade is not supposed to fall faster than Alice during a free fall. This is against the laws of physics.";

        //String user = "How long took the fall from Alice? How many minutes?";
        //String agent2 ="That is unknown. But given the amount of events during the fall, it would've been very long.";
        //String user2 = "She wasn't... She was afraid for the fall?";
        //String agent = "Alice was not afraid of the fall.";

        //String user = "Oh she is literally stuck in his as in she can't go through the door or window because she doesn't fit.";
        //String agent = "She does not fit through the door.";
        //String user = "hmm well I would like to meet some people in the real world that could pass me some of those very nice pieces of fruit and drinks yeah hmm well yeah so Alice goes to the palace you mentioned earlier or at least she tries to get there. Does she make it to the palace?";
        //String agent = "She arrives at the palace. There, she meets the queen.";

        String user = "there was another movie after";
        String agent = "Tim Burton made a movie in 2010";


        //adaptation.appendToHistory("ASNLG",agent3,"agent");
        adaptation.appendToHistory("ASNLG",user,"user");
        //adaptation.appendToHistory("ASNLG",agent2,"agent");
        //adaptation.appendToHistory("ASNLG",user2,"user");
        JsonArray modifications = adaptation.getAllModifications("ASNLG",agent);
        for(JsonObject m : modifications.getValuesAs(JsonObject.class)){
            ModifiedSentence ms = new ModifiedSentence(m);
            System.out.println(ms.toString());
        }
    }

    private static void testAll(){
        //Initialize the VA module
        String coreURL = "http://zilver015117.mobiel.utwente.nl:8080/";
        String agentUtterance = "I picked up the white kid gloves and ran after the white rabbit.";
        AlignmentGeneration adaptation = new AlignmentGeneration();

        //Append some conversation history
        adaptation.appendToHistory("JW","The white rabbit seemed in a hurry and dropped the white kid gloves.","agent");
        adaptation.appendToHistory("JW","Did you give back the gloves?","user");
        adaptation.appendToHistory("LG","The white rabbit seemed in a hurry and dropped the white kid gloves.","agent");
        adaptation.appendToHistory("LG","Did you give back the gloves?","user");

        //Test convergent
        JsonArray convergent = adaptation.getAllConvergent("LG",agentUtterance);
        for(JsonObject cMod : convergent.getValuesAs(JsonObject.class)){
            System.out.println(cMod);
        }

        //Test divergent
        JsonArray divergent = adaptation.getAllDivergent("LG",agentUtterance);
        for(JsonObject dMod : divergent.getValuesAs(JsonObject.class)){
            System.out.println(dMod);
        }

        //Test conversation history
        JsonArray conversationHistory = adaptation.getConversationHistory("JW");
        for(JsonValue turn : conversationHistory){
            System.out.println(turn);
        }

        //Test single modification
        JsonArray noHistoryModifications = adaptation.getModificationsNoHistory("What do you know about the hatter?","The mad hatter wears a <tm id=\\\"DMImpBegin\\\"/>top hat and loves to host tea parties.<tm id=\\\"DMImpEnd\\\"/>");
        for(JsonObject modifications : noHistoryModifications.getValuesAs(JsonObject.class)){
            System.out.println(modifications);
        }

        //Test deletion
        adaptation.deleteConversation("LG");

        //Test retrieving conversations
        JsonArray conversationIDs = adaptation.getConversationList();
        for(JsonValue id : conversationIDs){
            System.out.println(id);
        }

        //Test modifications
        JsonArray modifications = adaptation.getAllModifications("JW",agentUtterance);
        for(JsonObject m : modifications.getValuesAs(JsonObject.class)){
            ModifiedSentence ms = new ModifiedSentence(m);
            System.out.println(ms.toString());
        }

        //Test deletion all
        //adaptation.deleteAllConversationHistory();

        //Test retrieving conversations
        conversationIDs = adaptation.getConversationList();
        for(JsonValue id : conversationIDs){
            System.out.println(id);
        }

        adaptation.deleteConversation("JW");

    }

    private static void testJsonMapper() {

        String jsonString = "{\n" +
                "  \"template\": \"socialobligations_salutation_initial_y_01\",\n" +
                "  \"parameters\": [\n" +
                "    {\n" +
                "      \"var.var1\": \"human\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JsonReader jr = Json.createReader(new StringReader(jsonString));
        JsonObject fml = jr.readObject();
        Iterator<?> keys = fml.entrySet().iterator();

        while (keys.hasNext()) {
            String key = String.valueOf(keys.next());
            JsonStructure js = Json.createObjectBuilder().build();
            if (fml.get(key) instanceof JsonObject) {
                System.out.println("hi");
            }
            if (fml.get(key) instanceof JsonArray) {
                System.out.println("hello");
            }


        }
    }

}
