package eu.aria.dm.util;

/**
 * Created by WaterschootJB on 16-6-2017.
 */

    //    Copyright 2013 Petter T�rnberg
//
//    This demo code has been kindly provided by Petter T�rnberg <pettert@chalmers.se>
//    for the SentiWordNet website.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Sentiment {

        private Map<String, Double> dictionary;

        public Sentiment(String pathToSWN) throws IOException {
            // This is our main dictionary representation
            dictionary = new HashMap<String, Double>();

            // From String to list of doubles.
            HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap<String, HashMap<Integer, Double>>();

            BufferedReader csv = null;
            try {
                csv = new BufferedReader(new FileReader(pathToSWN));
                int lineNumber = 0;

                String line;
                while ((line = csv.readLine()) != null) {
                    lineNumber++;

                    // If it's a comment, skip this line.
                    if (!line.trim().startsWith("#")) {
                        // We use tab separation
                        String[] data = line.split("\t");
                        String wordTypeMarker = data[0];

                        // Example line:
                        // POS ID PosS NegS SynsetTerm#sensenumber Desc
                        // a 00009618 0.5 0.25 spartan#4 austere#3 ascetical#2
                        // ascetic#2 practicing great self-denial;...etc

                        // Is it a valid line? Otherwise, through exception.
                        if (data.length != 6) {
                            throw new IllegalArgumentException(
                                    "Incorrect tabulation format in file, line: "
                                            + lineNumber);
                        }

                        // Calculate synset score as score = PosS - NegS
                        Double synsetScore = Double.parseDouble(data[2])
                                - Double.parseDouble(data[3]);

                        // Get all Synset terms
                        String[] synTermsSplit = data[4].split(" ");

                        // Go through all terms of current synset.
                        for (String synTermSplit : synTermsSplit) {
                            // Get synterm and synterm rank
                            String[] synTermAndRank = synTermSplit.split("#");
                            String synTerm = synTermAndRank[0] + "#"
                                    + wordTypeMarker;

                            int synTermRank = Integer.parseInt(synTermAndRank[1]);
                            // What we get here is a map of the type:
                            // term -> {score of synset#1, score of synset#2...}

                            // Add map to term if it doesn't have one
                            if (!tempDictionary.containsKey(synTerm)) {
                                tempDictionary.put(synTerm,
                                        new HashMap<Integer, Double>());
                            }

                            // Add synset link to synterm
                            tempDictionary.get(synTerm).put(synTermRank,
                                    synsetScore);
                        }
                    }
                }

                // Go through all the terms.
                for (Map.Entry<String, HashMap<Integer, Double>> entry : tempDictionary
                        .entrySet()) {
                    String word = entry.getKey();
                    Map<Integer, Double> synSetScoreMap = entry.getValue();

                    // Calculate weighted average. Weigh the synsets according to
                    // their rank.
                    // Score= 1/2*first + 1/3*second + 1/4*third ..... etc.
                    // Sum = 1/1 + 1/2 + 1/3 ...
                    double score = 0.0;
                    double sum = 0.0;
                    for (Map.Entry<Integer, Double> setScore : synSetScoreMap
                            .entrySet()) {
                        score += setScore.getValue() / (double) setScore.getKey();
                        sum += 1.0 / (double) setScore.getKey();
                    }
                    score /= sum;

                    dictionary.put(word, score);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (csv != null) {
                    csv.close();
                }
            }
        }

        public double extract(String word, String pos) {
            Double sentiment = dictionary.get(word+"#"+pos);
            if(sentiment == null){
                //System.out.println("POS of wrong type");
                return 0;
            }
            return sentiment;
        }

    /**
     * Very simple method that calculates the sentiment (valence) of a sentence, based on verbs, nouns, adjectives and adverbs. Based
     * on sentiwordnet.
     * @param sentence, the given sentences to calculate the sentiment.
     * @return the sentiment of the given sentence
     */
    public double extractSentenceSentiment(String sentence){
            double sentence_sentiment = 0.0;
            Sentence sentiment_sentence = new Sentence(sentence);
            List<String> pos_tags = sentiment_sentence.posTags();
            int word_counter = 0;
            int sentiment_words = 0;
            for(String word : sentiment_sentence.words()){
                String tag = pos_tags.get(word_counter);
                tag = tag.substring(0,1).toLowerCase();
                String pos = "n";
                word = sentiment_sentence.lemma(word_counter);
                double sentiment = 0;
                switch (tag) {
                    case "n":
                        pos = "n";
                        sentiment = extract(word,pos);
                        sentence_sentiment = sentence_sentiment + sentiment;
                        sentiment_words++;
                        break;
                    case "j":
                        pos = "a";
                        sentiment = extract(word,pos);
                        sentence_sentiment = sentence_sentiment + sentiment;
                        sentiment_words++;
                        break;
                    case "r":
                        pos = "r";
                        sentiment = extract(word,pos);
                        sentence_sentiment = sentence_sentiment + sentiment;
                        sentiment_words++;
                        break;
                    case "v":
                        pos = "v";
                        sentiment = extract(word,pos);
                        sentence_sentiment = sentence_sentiment + sentiment;
                        sentiment_words++;
                        break;
                    default :
                        pos = "x";
                        break;
                }
                word_counter++;
            }
            sentence_sentiment = sentence_sentiment/sentiment_words;
            return sentence_sentiment;
        }

        public static void main(String [] args) throws IOException {


            if(args.length<1) {
                System.err.println("Usage: java SentiWordNetDemoCode <pathToSentiWordNetFile>");
                return;
            }

            String pathToSWN = Sentiment.class.getClassLoader().getResource(args[0]).getPath();
            Sentiment sentiwordnet = new Sentiment(pathToSWN);


            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNext()){
                String s = scanner.nextLine();
                double v = sentiwordnet.extractSentenceSentiment(s);
                System.out.println(v);
            }
        }

}
