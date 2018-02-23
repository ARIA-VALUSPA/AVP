package qamatcher;
import java.util.*;

/**
 * A Dialog is a list of question Strings and a list of AnswerTypes
 * Dialogs are the elements of a DialogStore
 */
public class Dialog{

  String id;

  List<AnswerType> answers;
  List<String> questions;

  public Dialog(String d){
    id = d;
    answers = new ArrayList<AnswerType>();
    questions = new ArrayList<String>();
  }

  public int answerSize(){ return answers.size();}
  public int questionSize(){ return questions.size();}

  public AnswerType getAnswer(int i){ return answers.get(i);}
  public String getQuestion(int i){ return questions.get(i);}

  public void addAnswer(AnswerType a){ answers.add(a);}
  public void addQuestion(String q){ questions.add(q);}

  public String xml(){
    String result = "<dialog id=\""+id+"\">\n";
    result += "<answerlist>\n";
    for (int i=0; i< answers.size();i++){
      result += (answers.get(i)).xml()+"\n";
    }
    result += "</answerlist>\n";
    result += "<questionlist>";
    for (int i=0; i< questions.size();i++){
      result += toXml(questions.get(i));
    }
    result += "</questionlist>\n";
    result += "</dialog>\n";
    return result;
  }

  private String toXml(String s){
    return "\n<question>"+s+"</question>";
  }

}