package qamatcher;

/**
 * Attribute is a pair of Strings (name, value)
 * A list of attributes is attached to the AnswerTypes of a Dialog
 *
 */
public class Attribute{

  private String att_name;
  private String att_value;

  public Attribute(String n, String v){
    att_name = n;
    att_value = v;
  }

  public String name(){ return att_name; }
  public String value(){ return att_value; }

  public String toString(){
    return att_name+"="+"\""+att_value+"\"";
  }
}