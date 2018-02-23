package qamatcher;

import java.util.*;
import java.io.*;
import java.text.DecimalFormat;


/**
 * contains a method similarity that measures how similar two sentences are
 * The methods return a value in [0,1] 
 * The method can be used to find for a given sentence
 * the best matching sentence in a set of sentences.
 * See method bestMatch
 */

public class ToolSet{

  public static String bestMatch(String str, List<String> lst){
    DecimalFormat df = new DecimalFormat("#,###,##0.00");
    System.out.println("Query :"+str);
    String result = lst.get(0);
    double simMax = similarity(str,result);
    System.out.println(df.format(simMax)+"\t"+lst.get(0));
    double sim;
    int len = lst.size();
    for (int i=1;i<len;i++){
      sim = similarity(str,lst.get(i));
      System.out.println(df.format(sim)+"\t"+lst.get(i));
      if (sim>simMax){
        result = lst.get(i);
        simMax = sim;
      }
    }
    return result;
  }

  /**
   *
   * @param sentence should has at least one string
   * @param maxGramSize should be 1 at least
   * @return set of continuous word n-grams up to maxGramSize from the sentence
   */
  public static List<String> generateNgramsUpto(String str, int maxGramSize) {

    List<String> sentence = Arrays.asList(str.split("[\\W+]"));

    List<String> ngrams = new ArrayList<String>();
    int ngramSize = 0;
    StringBuilder sb = null;

    //sentence becomes ngrams
    for (ListIterator<String> it = sentence.listIterator(); it.hasNext();) {
      String word = (String) it.next();

      //1- add the word itself
      sb = new StringBuilder(word);
      ngrams.add(word);
      ngramSize=1;
      it.previous();

      //2- insert prevs of the word and add those too
      while(it.hasPrevious() && ngramSize<maxGramSize){
        sb.insert(0,' ');
        sb.insert(0,it.previous());
        ngrams.add(sb.toString());
        ngramSize++;
      }

      //go back to initial position
      while(ngramSize>0){
        ngramSize--;
        it.next();
      }
    }
    return ngrams;
  }

  public static Set<String> intersection(List<String> lst1, List<String> lst2){
    Set<String> s1 = new TreeSet<String>(lst1);
    Set<String> s2 = new TreeSet<String>(lst2);
    s1.retainAll(s2);
    return s1;
  }

  public static Set<String> union (List<String> lst1, List<String> lst2){
    Set<String> s1 = new TreeSet<String>(lst1);
    Set<String> s2 = new TreeSet<String>(lst2);
    s1.addAll(s2);
    return s1;
  }

  public static double similarity(String str1, String str2){
    List<String> ngrams1 = ToolSet.generateNgramsUpto(str1, 3);
    List<String> ngrams2 = ToolSet.generateNgramsUpto(str2, 3);
    Set<String> interset = intersection(ngrams1,ngrams2);
    //System.out.println("Intersection="+interset.toString());
    Set<String> union = union(ngrams1,ngrams2);
    //System.out.println("Union="+union.toString());
    double len1 = interset.size();
    double len2 = union.size();
    return len1/len2;


  }


  public static List<String> readSentences(String filename){
    List<String> lst = new ArrayList<String>();
    String sent;
    try{
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      while ((sent=reader.readLine())!=null){
        lst.add(sent);
      }
      reader.close();
    }catch(IOException exc){
      System.out.println(exc.getMessage());
    }
    return lst;
  }



  public static void testing(){
    List<Integer> original = Arrays.asList(12,16,17,19,101);
    List<Integer> selected = Arrays.asList(16,19,107,108,109);

    ArrayList<Integer> add = new ArrayList<Integer>(selected);
    add.removeAll(original);
    System.out.println("Add: " + add);

    ArrayList<Integer> remove = new ArrayList<Integer>(original);
    remove.removeAll(selected);
    System.out.println("Remove: " + remove);
  }

  public static void main(String[] args){
/**
 long startTime = System.currentTimeMillis();
 List<String> ngrams = ToolSet.generateNgramsUpto("This is my car.", 3);
 long stopTime = System.currentTimeMillis();
 System.out.println("My time = "+(stopTime-startTime)+" ms with ngramsize = "+ngrams.size());
 System.out.println(ngrams.toString());

 System.out.println("Testing...");
 testing();
 //Add: [107, 108, 109]
 //Remove: [12, 17, 101]


 List<String> ngrams1 = ToolSet.generateNgramsUpto("This is really not my car.", 3);
 List<String> ngrams2 = ToolSet.generateNgramsUpto("This is my car.", 3);
 Set<String> interset = intersection(ngrams1,ngrams2);
 System.out.println("Intersection="+interset.toString());
 Set<String> union = union(ngrams1,ngrams2);
 System.out.println("Union="+union.toString());

 String example1 = "Hoe heet jij ?";
 String example2 = "Wat is jouw naam ?";
 String example3 = "Hoe heet u ?";
 System.out.println("Similarity:"+ similarity(example1,example2));
 System.out.println("Similarity:"+ similarity(example1,example3));
 System.out.println("Similarity:"+ similarity(example2,example3));
 System.out.println("Similarity:"+ similarity(example2,example2));

 List<String> lijst = new ArrayList<String>();
 lijst.add("How are you ?");
 lijst.add("Who are you ?");
 lijst.add("Where are you ?");
 lijst.add("Do you know what time it is ?");
 lijst.add("Do you know the time ?");
 lijst.add("Do you know where we are ?");
 */
    String query = "Hoe laat was het ?";
    List<String> lijst = readSentences("questions.txt");
    System.out.println("Best match :"+ bestMatch(query,lijst));
  }

}