package eu.aria.dm.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Manon Plagnol
 * 
 * Transform the sentences tip by the user into keyword which can be understand and used by the program
 * 
 */
public class SentencesToKeywords {
    private final String posUser = "0";
    // public final String[] punctuation = {"!", "?", ".", "...", ","};
    private final RemoverStopWord rm;
    private final SynonymReplacement sr;
    private final StanfordTagger st;

    public SentencesToKeywords(String stopwordsPath, String synonymsPath, String posModelPath) throws IOException {
        
        rm = new RemoverStopWord(stopwordsPath);
        sr = new SynonymReplacement(synonymsPath);
        st = new StanfordTagger(posModelPath);
    }


    /*
    Extract words from sentence in a certain order and avoid repetition :
    1 - Proper Nouns and "bye" 
    2 - Nouns (singular or plural)
    3 - Cardinal number
    4 - Verbs and Preposition or subordinating conjunction
    5 - Adjective
    6 - Interjection, yes and no, interrogation mark and Wh­adverb ( = How) to detect "how are you ?" etc in state H to go to hay
    */
    public ArrayList<String> pickUp(ArrayList<String> sentence/*, BufferedWriter bwDet*/) throws IOException {
        ArrayList<String> wordsForKeywords = new ArrayList<>();
        sentence = sr.replaceBySynonyme(sentence);
        ArrayList<String> wtt = st.tagFile(sentence);
        //bwDet.write("After removing of stopwords and Standford tagger, userSay =  " + wtt.toString() + "\n");

        pickNNP(wtt, wordsForKeywords);
        pickNNandNNS(wtt, wordsForKeywords);
        pickCD(wtt, wordsForKeywords);
        pickVBandIN(wtt, wordsForKeywords);
        pickJJ(wtt, wordsForKeywords);
        pickRest(wtt, wordsForKeywords);

/*
        if (wordsForKeywords.contains("information") && wordsForKeywords.contains("opinion"))
            wordsForKeywords.remove("opinion");
*/
        //bwDet.write("Information extracted from userSay = " + wordsForKeywords.toString() + "\n");
        //bwDet.flush();
        return wordsForKeywords;
    }

    /*
    Extract interjection, yes, no, interrogation mark
    */
    private void pickRest(ArrayList<String> wtt, ArrayList<String> wordsForKeywords) {
        for (String w : wtt) {
            String[] temp = w.split("_");
            if (temp.length == 2) {
                temp[1] = temp[1].trim();
                if ((temp[1].contains("UH") || (temp[0].trim()).equals("yes") || (temp[0].trim()).equals("no") || (temp[0].trim()).contains("?")) && !wordsForKeywords.contains(temp[0].trim())) {
                    wordsForKeywords.add(temp[0].toLowerCase());
                }
                else if (temp[1].equals("WRB"))
                    wordsForKeywords.add(0, temp[0].toLowerCase());

            }
        }
    }

    /*
    Extract asjectives
    */
    private void pickJJ(ArrayList<String> wtt, ArrayList<String> wordsForKeywords) {
        for (String w : wtt) {
            String[] temp = w.split("_");
            if (temp.length == 2) {
                temp[1] = temp[1].trim();
                if ((temp[1].contains("JJ")) && !wordsForKeywords.contains(temp[0].trim())) {
                    wordsForKeywords.add(temp[0].toLowerCase());
                }
            }
        }
    }

    /*
    Extract verbs and conjonctions
    */
    private void pickVBandIN(ArrayList<String> wtt, ArrayList<String> wordsForKeywords) {
        for (String w : wtt) {
            String[] temp = w.split("_");
            if (temp.length == 2) {
                temp[1] = temp[1].trim();
                if ((temp[1].contains("VB") || temp[1].contains("IN")) && !wordsForKeywords.contains(temp[0].trim())) {
                    wordsForKeywords.add(temp[0].toLowerCase());
                }
            }
        }
    }

    /*
    Extract numbers
    */
    private void pickCD(ArrayList<String> wtt, ArrayList<String> wordsForKeywords) {
        for (String w : wtt) {
            String[] temp = w.split("_");
            if (temp.length == 2) {
                temp[1] = temp[1].trim();
                if (temp[1].equals("CD")) {
                    wordsForKeywords.add(temp[0].toLowerCase());
                }
            }
        }
    }


    /*
        Extract nouns
        */
    private void pickNNandNNS(ArrayList<String> wtt, ArrayList<String> wordsForKeywords) {
        for (String w : wtt) {
            String[] temp = w.split("_");
            if (temp.length == 2) {
                temp[1] = temp[1].trim();
                if ((temp[1].equals("NN") || temp[1].equals("NNS")) && !wordsForKeywords.contains(temp[0].trim())) {
                    wordsForKeywords.add(temp[0].toLowerCase());
                }
            }
        }
    }

    /*
        Extract proper nouns
        */
    private void pickNNP(ArrayList<String> wtt, ArrayList<String> wordsForKeywords) {
        for (String w : wtt) {
            String[] temp = w.split("_");
            if (temp.length == 2) {
                temp[1] = temp[1].trim();
                if (((temp[1].equals("NNP")) && !wordsForKeywords.contains(temp[0].trim())) || temp[0].trim().equals("bye")) {
                    wordsForKeywords.add(temp[0].toLowerCase());
                }

            }
        }
    }

    /*
        Extract certain word knowing the tag of the standford tagger - Used to extract the user data in introduce (name, surname, age etc...)
    */
    public String pickUpSearch(ArrayList<String> wordsList, String search) {
        String s = "";
        ArrayList<String> wordsForKeywords = new ArrayList<>();
        for (String line : wordsList) {
            s = s + line;
        }

        String[] wordstagged = s.split(" ");
        for (String w : wordstagged) {
            String[] temp = w.split("_");
            if (temp[1].equals(search)) {
                wordsForKeywords.add(temp[0]);
            }
        }

        return wordsForKeywords.get(0);
    }

    /*
    Extract certain word knowing the tag of the standford tagger - Used to extract the user data in introduce (name, surname, age etc...)
   */
    public String convertToKeywords(String s, String search) {
        String ss;
        ss = pickUpSearch(st.tagFile(rm.remove(sr.replaceBySynonyme(s))), search);
        return ss.trim();
    }

    /*
    Remove stopwords from the sentence input by the user
    */
    public ArrayList<String> removeStopWords(String userSay) {
        return rm.remove(userSay);
    }

    public boolean isFollowUp(ArrayList<String> sentence) {
        //sentence = sr.replaceBySynonyme(sentence);
        ArrayList<String> wtt = st.tagFile(sentence);
        for (String w : wtt) {
            String[] temp = w.split("_");
            if (temp.length == 2) {
                //System.out.println("NN?:"+temp[1]+"--"+temp[0]);
                temp[1] = temp[1].trim();
                if (temp[1].startsWith("NN")) {
                    return false;
                }
            }
        }
        return true;
    }
}
