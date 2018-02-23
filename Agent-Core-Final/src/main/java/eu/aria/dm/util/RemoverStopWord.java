package eu.aria.dm.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author psamp3
 * Remove stop word list in Data/stopwords.txt of a sentences 
 * 
 */
public class RemoverStopWord {

    public final ArrayList<String> stopwordsList;
    public static ArrayList<String> wordsList;

    RemoverStopWord(String stopwordPath) throws IOException {
        RemoverStopWord.wordsList = new ArrayList<>();
        stopwordsList = new ArrayList<>();

        File f = new File(stopwordPath);
        try (BufferedReader br = new BufferedReader(new FileReader(f))){
            String line;
            while ((line = br.readLine()) != null) {
                stopwordsList.add(line);
            }
        }
    }

    public ArrayList<String> remove(String s) {
        wordsList.clear();
        s = s.trim().replaceAll("\\s+", " ");
        s = s.replaceAll("\\,", " ,");
        String[] words = s.split(" ");

        wordsList.addAll(Arrays.asList(words));
        for (int i = 0; i < wordsList.size(); i++) {
            // get the item as string

            if (stopwordsList.contains(wordsList.get(i).toLowerCase())) {
                wordsList.remove(i);
                i = i - 1;
            }
        }
        return wordsList;
    }

    public ArrayList<String> remove(ArrayList<String> wordsList) {

        for (int i = 0; i < wordsList.size(); i++) {
            // get the item as string

            if (stopwordsList.contains((wordsList.get(i)).toLowerCase())) {
                wordsList.remove(i);
                i = i - 1;
            }

        }
        return wordsList;
    }
}
