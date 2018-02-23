package qamatcher;
import java.util.*;

/**
 * AnswerType of a Dialog is an answer String with a set of attribute names and value pairs
 */
public class AnswerType{

  private Vector<Attribute> attributes;
  public String answer;

  public AnswerType(){
    attributes = new Vector<Attribute>();
  }

  public void setText(String t){ answer = t;}

  public void addAttribute(Attribute at){ attributes.add(at);}
  public void addAttribute(String n, String v){
    addAttribute(new Attribute(n,v));
  }

  /**
   * @return the String value of the given attribute
   * @return null if no such attribute name exists
   */
  public String valueOfAttribute(String attN){
    for(int i=0;i<attributes.size();i++){
      Attribute att = attributes.get(i);
      if (att.name().equals(attN))
        return att.value();
    }
    return null;
  }

  public int size(){ return attributes.size();}

  public String xml(){
    String result="";
    result = "<answer "+ attstring() +">";
    result += answer;
    result += "</answer>";
    return result;
  }

  private String attstring(){
    String result="";
    for (int i=0; i<size();i++){
      result += attributes.get(i);
    }
    return result;
  }

}