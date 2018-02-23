package qamatcher;

/**
 * This class can be used for testing a matching method
 * It requires a simple text file with sentences
 */
public class TestQAMatcher{

  public static void main(String[] args){
    String filename = "questions.txt";  // should be stored in the resource/qamatcher directory
    QuestionMatcher qam= new QuestionMatcher(filename);
    //new QuestionGUI(qam);
    String query = "Hallo";
    System.out.print("Question:");
    System.out.flush();
    while ((query = Console.readString()) !=""){
      System.out.println("Best match :"+ qam.bestMatch(query));
      System.out.print("Question:");
    }

  }


}