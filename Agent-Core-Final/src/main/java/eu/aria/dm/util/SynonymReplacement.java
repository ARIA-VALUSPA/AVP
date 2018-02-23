package eu.aria.dm.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author psamp3
 */
public class SynonymReplacement {

    private final HashMap<Integer, ArrayList<String>> synonyms;


    SynonymReplacement(String synonymesPath) throws IOException {
        synonyms = new HashMap<>();
        File f = new File(synonymesPath);
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int j = 0;
            while ((line = br.readLine()) != null) {
                ArrayList<String> syn = new ArrayList<>(Arrays.asList(line.split(" ")));
                synonyms.put(j, syn);
                j++;
            }
        }
    }


    public ArrayList<String> replaceBySynonyme(ArrayList<String> wordsList) {

        for (int i = 0; i < wordsList.size(); i++) {
            // get the item as string
            for (int j = 0; j < synonyms.size(); j++) {
                ArrayList<String> temp = synonyms.get(j);

                if (temp.contains((wordsList.get(i)).toLowerCase())) {
                    if (wordsList.get(i).toLowerCase().equals("what") && wordsList.contains("think")) {
                        wordsList.set(i, temp.get(0));
                        wordsList.remove("think");
                    } else
                        wordsList.set(i, temp.get(0));
                }
            }

        }
        return wordsList;
    }


    public ArrayList<String> replaceBySynonyme(String s) {
        ArrayList<String> wordsList = new ArrayList<>();
        s = s.trim();
        s = s.replace("?", " ?");
        s = s.replace(",", " ,");
        s = s.replace("!", " !");
        s = s.replace(".", " .");
        s = s.trim();
        String[] words = s.split(" ");

        wordsList.addAll(Arrays.asList(words));

        if (!wordsList.get(wordsList.size() - 1).equals("?") && !wordsList.get(wordsList.size() - 1).equals("!") &&
                !wordsList.get(wordsList.size() - 1).equals(".") && !wordsList.get(wordsList.size() - 1).equals("...")) {
            wordsList.add(" ");
            wordsList.add(".");
        }

        for (int i = 0; i < wordsList.size(); i++) {
            // get the item as string
            for (int j = 0; j < synonyms.size(); j++) {
                ArrayList temp = synonyms.get(j);
                if (temp.contains((wordsList.get(i)).toLowerCase())) {
                    wordsList.set(i, (String) temp.get(0));
                }
            }

        }
        return wordsList;
    }
}
