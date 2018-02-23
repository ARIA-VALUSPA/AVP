package qamatcher;

import java.io.IOException;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * DomDialogParser is created with an xml file that contains the specification
 * of questions and answer pairs.
 * The file is stored in the resources/qamatcher direcotory
 * which should be on the class path
 * The DialogStore can be obtained by the method getDialogStore()
 */
public class DomDialogsParser{

  DialogStore myDialogs;
  Document dom;
  String xmlFileName;

  /**
   * create a new and load a DialogStore
   * @param fn the xml file name
   */
  public DomDialogsParser(String fn){
    //create a store to hold the Dialog objects
    xmlFileName = fn;
    myDialogs = new DialogStore();
    loadStore();
  }
  
   public DomDialogsParser(String fn, String df){
    //create a store to hold the Dialog objects
    xmlFileName = fn;
    myDialogs = new DialogStore(df);
    loadStore();
  }

  /**
   * @return the DialogStore
   */
  public DialogStore getDialogStore(){
    return myDialogs;
  }

  public void loadStore() {
    //parse the xml file and get the dom object
    parseXmlFile(xmlFileName);
    //get each dialog element and create a Dialog object
    // and add this to the DialogStore
    parseDocument();
  }



  private void parseXmlFile(String fileName){
    //get the factory
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    try {

      //Using factory get an instance of document builder
      DocumentBuilder db = dbf.newDocumentBuilder();

      //parse using builder to get DOM representation of the XML file

      dom = db.parse(getXMLFile(fileName));


    }catch(ParserConfigurationException pce) {
      pce.printStackTrace();
    }catch(SAXException se) {
      se.printStackTrace();
    }catch(IOException ioe) {
      ioe.printStackTrace();
    }
  }


  public File getXMLFile(String filename){
//	File f =null;
//	try{
//		java.net.URL fileURL = DomDialogsParser.class.getResource(filename);
//		System.out.println("fileURL="+fileURL);
//        	if (fileURL != null) {
//        		java.net.URI fileURI = fileURL.toURI();
//			f = new File(fileURI);
//		} else {
//            		System.err.println("Couldn't find file: " + filename);
//        	}
//	}catch(URISyntaxException exc){
//		System.out.println(exc.getMessage());
//	}
//	return f;
    File f = null;
    if(filename != null) {
      f = new File(filename);
    } else {
      System.err.println("Couldn't find file: " + filename);
    }

    return f;

  }



  private void parseDocument(){
    //get the root elememt
    Element docEle = dom.getDocumentElement();

    //get a nodelist of <dialog> elements
    NodeList nl = docEle.getElementsByTagName("dialog");
    if(nl != null && nl.getLength() > 0) {
      for(int i = 0 ; i < nl.getLength();i++) {

        //get the dialog element
        Element el = (Element)nl.item(i);

        //get the Dialog object
        Dialog d = getDialog(el);

        //add it to list
        myDialogs.add(d);
      }
    }
  }


  /**
   * take an dialog element and read the values in, create
   * a Dialog object and return it
   * @param el
   * @return
   */
  private Dialog getDialog(Element el) {
    //for each <dialog> element get text or int values of id
    String id = el.getAttribute("id");
    //Create a new Dialog with the values read from the xml nodes
    Dialog d = new Dialog(id);
    Element answerlistelement=null;
    NodeList nl = el.getElementsByTagName("answerlist");
    if(nl != null && nl.getLength() > 0) {
      answerlistelement = (Element)nl.item(0);
      getAnswerList(answerlistelement,d);
    }
    Element questionlistelement = null;
    NodeList nl2 = el.getElementsByTagName("questionlist");
    if(nl2 != null && nl2.getLength() > 0) {
      questionlistelement = (Element)nl2.item(0);
      getQuestionList(questionlistelement,d);
    }
    return d;
  }

  // create and add AnswersType objects to dialog d
  private void getAnswerList(Element el, Dialog d){
    NodeList nl = el.getElementsByTagName("answer");
    if(nl != null && nl.getLength() > 0) {
      for(int i = 0 ; i < nl.getLength();i++) {
        //get the answer element
        Element ela = (Element)nl.item(i);
        //get the AnswerType object
        AnswerType ans = getAnswerType(ela);
        //add it to dialog
        d.addAnswer(ans);
      }
    }
  }

  private AnswerType getAnswerType(Element ela){
    AnswerType ans = new AnswerType();
    String anstype = ela.getAttribute("type");
    String attname = "type";
    String attvalue = anstype;
    ans.addAttribute(attname,attvalue);
    String text = ela.getFirstChild().getNodeValue();
    ans.setText(text);
    return ans;
  }

  // create and add questions string to dialog d
  private void getQuestionList(Element el, Dialog d){
    //get a nodelist of <question> elements
    NodeList nl = el.getElementsByTagName("question");
    if(nl != null && nl.getLength() > 0) {
      for(int i = 0 ; i < nl.getLength();i++) {
        //get the question element
        Element elq = (Element)nl.item(i);
        //String question = getTextValue(elq,"question");
        //String question = getString(elq,"question");
        String question = elq.getFirstChild().getNodeValue().toLowerCase();
        //add it to dialog
        //System.out.println(question);
        d.addQuestion(question);
      }
    }
  }


  private String getString(Element element, String tagName) {
    NodeList list = element.getElementsByTagName(tagName);
    if (list != null && list.getLength() > 0) {
      NodeList subList = list.item(0).getChildNodes();
      if (subList != null && subList.getLength() > 0) {
        return subList.item(0).getNodeValue();
      }
    }
    return null;
  }


  /**
   * I take a xml element and the tag name, look for the tag and get
   * the text content
   * i.e for <employee><name>John</name></employee> xml snippet if
   * the Element points to employee node and tagName is name I will return John
   * @param ele
   * @param tagName
   * @return
   */
  private String getTextValue(Element ele, String tagName) {
    String textVal = null;
    NodeList nl = ele.getElementsByTagName(tagName);
    if(nl != null && nl.getLength() > 0) {
      Element el = (Element)nl.item(0);
      textVal = el.getFirstChild().getNodeValue();
    }
    return textVal;
  }


  /**
   * Calls getTextValue and returns a int value
   * @param ele
   * @param tagName
   * @return
   */
  private int getIntValue(Element ele, String tagName) {
    //in production application you would catch the exception
    return Integer.parseInt(getTextValue(ele,tagName));
  }

  /**
   * Print the DialogStore to console
   */
  private void printStore(){
    System.out.println("No of Dialogs '" + myDialogs.size() + "'.");
    System.out.println(myDialogs.xml());
  }


  public static void main(String[] args){
    String fileName = "vragen.xml";
    DomDialogsParser dpe = new DomDialogsParser(fileName);
    dpe.printStore();
  }

}
