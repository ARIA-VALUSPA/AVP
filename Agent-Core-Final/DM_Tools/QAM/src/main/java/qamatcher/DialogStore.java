package qamatcher;
import java.util.*;
import java.io.*;
import java.nio.file.Paths;
import org.apache.commons.lang3.tuple.Pair;

/**
 * DialogStore is a store containing Dialog elements  
 * that contains matching questiona and answers pairs
 * The main public method is bestMatch.
 * The DialogStore is created and filled by reading an xml file with a DomDialogParser
 */
public class DialogStore{

  List<Dialog> dialogs;
  private static final String DEFAULT_ANSWER = "I do not understand what you mean.";

  ArrayList<String> defaultAnswers = new ArrayList<String>();

  public DialogStore(){
    dialogs = new ArrayList<Dialog>();

    // Load dictionary of default answers
    //String dafilename = System.getProperty("user.dir") + "\\resources\\defaultanswers.txt";
    String dafilename = "defaultanswers.txt";
    //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
    System.out.println("Default replies: " + dafilename);
    try {
      BufferedReader br = new BufferedReader(new FileReader(dafilename));
      String line;
      while ((line = br.readLine()) != null)
      {
        defaultAnswers.add(line);
      }
    }
    catch (FileNotFoundException e)
    {
      System.out.println("Dialogue store: file not found");
    }
    catch (IOException e)
    {
      System.out.println("IOexception");
    }

    //defaultAnswers.add("Huh?");
    //defaultAnswers.add("What?");
    //System.out.println(Arrays.toString(defaultAnswers.toArray()));
  }
  
   public DialogStore(String dr_file){
    dialogs = new ArrayList<Dialog>();

    // Load dictionary of default answers
    //String dafilename = System.getProperty("user.dir") + "\\resources\\defaultanswers.txt";
    String dafilename = dr_file;
    //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
    System.out.println("Default replies: " + dr_file);
    try {
      BufferedReader br = new BufferedReader(new FileReader(dafilename));
      String line;
      while ((line = br.readLine()) != null)
      {
        defaultAnswers.add(line);
      }
    }
    catch (FileNotFoundException e)
    {
      System.out.println("Dialogue store: file not found");
    }
    catch (IOException e)
    {
      System.out.println("IOexception");
    }

    //defaultAnswers.add("Huh?");
    //defaultAnswers.add("What?");
    //System.out.println(Arrays.toString(defaultAnswers.toArray()));
  }

  //get a random default answer from the list of default answers
  public String RandomDefaultAnswer()
  {
    if (defaultAnswers.size() == 0)
    {
      return DEFAULT_ANSWER;
    }

    return defaultAnswers.get((int)(Math.random() * (defaultAnswers.size())));
  }

  public void add(Dialog d){ dialogs.add(d);
  }

  public int size(){
    return dialogs.size();
  }


  public String xml(){
    String result="<dialoglist>\n";
    for (int i=0; i< dialogs.size();i++){
      result += (dialogs.get(i)).xml();
    }
    result += "</dialoglist>\n";
    return result;
  }

  /**
   * @return String that is answer of dialog with given id and attribute name and attribute value
   */
  public String answerString(Dialog d, String attName, String attValue){
    if (d == null) {return RandomDefaultAnswer();};
    for(int j=0;j<d.answerSize();j++){
      AnswerType at = d.getAnswer(j);
      String value = at.valueOfAttribute(attName);
      if ((value!=null) && (value.equals(attValue)))
        return at.answer;
    }
    return RandomDefaultAnswer();
  }

  /**
   * @return String that is best answer to given question in a dialog that satisfies given name and attribute value
   */
  public String bestMatch(String question, String attName, String attValue){
    String answer = "";
    Dialog d = getBestMatchingDialog(question);
    if (d!=null){
      answer = answerString(d, attName , attValue );
    }else{
      answer = RandomDefaultAnswer();
    }
    return answer;
  }

  /**
   * @return the Dialog in this DialogStore with a question that best matches the given query
   */
  public Dialog getBestMatchingDialog(String query){
    Dialog bestDialog = null;
    double bestMatch = -0.1;
    for(int i=0;i<dialogs.size();i++){
      Dialog d = dialogs.get(i);
      for(int j=0;j<d.questionSize();j++){
        String q = d.getQuestion(j);
        double match = similarity(query, q);
        if (match>bestMatch){
          bestMatch = match;
          bestDialog = d;
        }
      }
    }
    if (bestMatch == 0) {
      return null;
    }
    return bestDialog;
  }
  
  /**
     * @param query, the user query
   * @return the Dialog in this DialogStore with a question that best matches the given query
   */
  public Pair<Dialog,Double> getBestMatchingDialogAndScore(String query){
    Dialog bestDialog = null;
    double bestMatch = -0.1;
    for(int i=0;i<dialogs.size();i++){
      Dialog d = dialogs.get(i);
      for(int j=0;j<d.questionSize();j++){
        String q = d.getQuestion(j);
        double match = similarity(query, q);
        if (match>bestMatch){
          bestMatch = match;
          bestDialog = d;
        }
      }
    }
    if (bestMatch == 0) {
      return Pair.of(null, 0.0);
    }
    return Pair.of(bestDialog,bestMatch);
  }
  
  /**
   * Method for retrieving all queries with their matching score
   * @param query, the user query
   * @return , the list of queries with similarity queries to the user query
   */
  public List<Pair<Dialog, Double>> retrieveQueries(String query){
      
      List<Pair<Dialog,Double>> queries = new ArrayList();      
      for(int i=0;i<dialogs.size();i++){
          Dialog d = dialogs.get(i);
          double max = 0.0;
          for(int j=0;j<d.questionSize();j++){
              String q = d.getQuestion(j);
              double match = similarity(query,q);
              if(match > max)
                  max = match;
          }
          queries.add(Pair.of(d,max));
      }
      Collections.sort(queries,new DialogComparator().reversed());
      return queries;
  }
  

  /**
   * Computes similarity between two Strings
   * The current implementation computes the relative size of the intersection of the sets of n-grams
   * of words in the two given strings
   * @return a value in [0,1] the similarity between two given Strings str1 and str2
   */
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

  private static Set<String> intersection(List<String> lst1, List<String> lst2){
    Set<String> s1 = new TreeSet<String>(lst1);
    Set<String> s2 = new TreeSet<String>(lst2);
    s1.retainAll(s2);
    return s1;
  }

  private static Set<String> union (List<String> lst1, List<String> lst2){
    Set<String> s1 = new TreeSet<String>(lst1);
    Set<String> s2 = new TreeSet<String>(lst2);
    s1.addAll(s2);
    return s1;
  }
  
  public class DialogComparator implements Comparator<Pair<Dialog,Double>>{
      @Override
      public int compare(Pair<Dialog,Double> p1, Pair<Dialog,Double> p2){
          return p1.getValue().compareTo(p2.getValue());
      }
  }

}