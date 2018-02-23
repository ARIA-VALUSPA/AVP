/* 
 * By Merijn Bruijnes, 
 * Human Media Interaction, University of Twente
 * for the ARIA VALUSPA project
 */

package eu.aria.dmtools.dialogstructurecreator;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.JFileChooser;

import net.miginfocom.swing.MigLayout;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Objects;
import java.beans.PropertyChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//next time look at GSON 
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;


public class CMoveCreatorGUI  {	
	public static String moveName;
	//strings to store id from gui
	public static String idEpisode;
	public static String idExchange;
	public static String idMove;
	public static String idComments;
	
	//strings for agent behaviours
	public static String AU;
	public static String AUcomments;
	public static String agentNVbehaviour;
	public static String agentNVbehaviourComments;
	
	//strings for situation
	public static String UU;
	public static String UUcomments;
	public static String rules;
	public static String rulesComments;
	
	//strings for move parameters
	public static String moveType = "C";
	public static String moveTag = "C";
	
	public static JSONObject move = new JSONObject();
	
	static JTextField textField_idEpisode = new JTextField();
	static JTextField textField_idExchange = new JTextField();
	static JTextField textField_idMove = new JTextField();
	static JTextField textField_idComments = new JTextField();
	static JTextArea textArea_AU = new JTextArea();
	static JTextArea textArea_AUcomments = new JTextArea();
	static JTextArea textArea_agentNVbehaviour = new JTextArea();
	static JTextArea textArea_agentNVbehaviourComments = new JTextArea();
	static JTextArea textArea_UU = new JTextArea();
	static JTextArea textArea_UUcomments = new JTextArea();
	static JTextArea textArea_rules = new JTextArea();
	static JTextArea textArea_rulesComments = new JTextArea();
	
	static JRadioButton rdbtnO = new JRadioButton("O");
	static JRadioButton rdbtnM = new JRadioButton("M");
	static JRadioButton rdbtnC = new JRadioButton("C");
	
	private static final ButtonGroup buttonGroup = new ButtonGroup();
	
	
	
	//add the value from the textfield/area to the appropriate string
	public static void addToString(String property, String value){
		//System.out.println("property '"+property+"' has value: "+value);
		if(property.equals("textField_idEpisode")) idEpisode = value;
		if(property.equals("textField_idExchange")) idExchange = value;
		if(property.equals("textField_idMove")) idMove = value;
		if(property.equals("textField_idComments")) idComments = value;
		if(property.equals("textArea_AU")) AU = value;
		if(property.equals("textArea_AUcomments")) AUcomments = value;
		if(property.equals("textArea_agentNVbehaviour")) agentNVbehaviour = value;
		if(property.equals("textArea_agentNVbehaviourComments")) agentNVbehaviourComments = value;
		if(property.equals("textArea_UU")) UU = value;
		if(property.equals("textArea_UUcomments")) UUcomments = value;
		if(property.equals("textArea_rules")) rules = value;
		if(property.equals("textArea_rulesComments")) rulesComments = value;
	}
	
	//create the move and put it to a file (or whatever else we think of later)
	public static void writeMove(){
		moveName = idEpisode+"."+idExchange+"."+idMove;
		System.out.println("Creating move: "+moveName);	
		
		JSONObject item = new JSONObject();
		JSONObject comments = new JSONObject();
		JSONArray ja = new JSONArray();
		
		item.put("Name", moveName);
		item.put("Goal", idMove);
		item.put("Type", moveType);
		item.put("Tag", moveTag);
		item.put("UU", UU);
		item.put("AB", agentNVbehaviour);
		item.put("rules", rules);
		item.put("AU", AU);
		
		comments.put("idComments", idComments);
		comments.put("AUcomments", AUcomments);
		comments.put("agentNVbehaviourComments", agentNVbehaviourComments);
		comments.put("UUcomments", UUcomments);
		comments.put("rulesComments", rulesComments);
		
		ja.add(comments);
		ja.add(item);
		move.put("Move", ja);

		
		try(  PrintWriter out = new PrintWriter(moveName+".json")){
		    out.println(move.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("MoveToString: "+move.toString());
	}
	
	public static void readMove(File file){
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(new FileReader(file));
			JSONArray move = (JSONArray) obj.get("Move");
			JSONObject comments = (JSONObject) move.get(0);
			JSONObject properties = (JSONObject) move.get(1);
					
			moveName = (String) properties.get("Name");
			
			textField_idEpisode.setText(moveName.split("\\.")[0]);
	 		textField_idExchange.setText(moveName.split("\\.")[1]);
	 		textField_idMove.setText(					(String) properties.get("Goal"));
	 		textField_idComments.setText(				(String) comments.get("idComments"));
	 		textArea_AU.setText(						(String) properties.get("AU"));  
	 		textArea_AUcomments.setText(				(String) comments.get("AUcomments"));
	 		textArea_agentNVbehaviour.setText(			(String) properties.get("AB"));
	 		textArea_agentNVbehaviourComments.setText(	(String) comments.get("agentNVbehaviourComments"));
	 		textArea_UU.setText(						(String) properties.get("UU"));
	 		textArea_UUcomments.setText(				(String) comments.get("UUcomments"));
	 		textArea_rules.setText(						(String) properties.get("rules"));
	 		textArea_rulesComments.setText(				(String) comments.get("rulesComments"));			
	 		
	 		moveTag = (String) properties.get("Tag");
	 		//set tag-radiobuttons
			if (moveTag.equals("C")){ rdbtnC.setSelected(true); }
			if (moveTag.equals("O")){ rdbtnO.setSelected(true); }
	 		if (moveTag.equals("M")){ rdbtnM.setSelected(true); }
	 		
	 		// TODO moveType = (String) move.get("Type");
	 		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                 //createAndShowGUI();
            	 //System.out.println("starting");            	 
            	 
            	 JFrame frame = new JFrame("Dialogue Creator GUI - Create a C-Move");
            	 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            	 frame.getContentPane().setLayout(new MigLayout("", "[][][207.00,grow][][208.00,grow][][203.00,grow][11.00][212.00,grow]", "[][][][grow][][grow][][grow][][grow][]"));
            	 
            	 JLabel lblVariable = new JLabel("episode");
            	 frame.getContentPane().add(lblVariable, "cell 2 0,alignx center");
            	 
            	 JLabel lblExchange = new JLabel("exchange");
            	 frame.getContentPane().add(lblExchange, "cell 4 0,alignx center");
            	 
            	 JLabel lblMove = new JLabel("move");
            	 frame.getContentPane().add(lblMove, "cell 6 0,alignx center");
            	 
            	 JLabel lblComments = new JLabel("comments");
            	 frame.getContentPane().add(lblComments, "cell 8 0,alignx center");
            	 
            	 JLabel lblId = new JLabel("id");
            	 lblId.setHorizontalAlignment(SwingConstants.CENTER);
            	 frame.getContentPane().add(lblId, "cell 0 1,alignx center,aligny center");
            	 
            	 
            	 addChangeListener(textField_idEpisode, e -> addToString("textField_idEpisode",textField_idEpisode.getText()));
            	 frame.getContentPane().add(textField_idEpisode, "cell 2 1,growx");
            	 textField_idEpisode.setColumns(10);
            	 
            	 JLabel label = new JLabel(" . ");
            	 frame.getContentPane().add(label, "cell 3 1,alignx trailing");
            	 
            	 
            	 addChangeListener(textField_idExchange, e -> addToString("textField_idExchange",textField_idExchange.getText()));
            	 frame.getContentPane().add(textField_idExchange, "cell 4 1,growx");
            	 textField_idExchange.setColumns(10);
            	 
            	 JLabel label_1 = new JLabel(" . ");
            	 frame.getContentPane().add(label_1, "cell 5 1,alignx trailing");
            	 
            	 
            	 addChangeListener(textField_idMove, e -> addToString("textField_idMove",textField_idMove.getText()));
            	 frame.getContentPane().add(textField_idMove, "cell 6 1,growx");
            	 textField_idMove.setColumns(10);
            	 
            	 
            	 addChangeListener(textField_idComments, e -> addToString("textField_idComments",textField_idComments.getText()));
            	 frame.getContentPane().add(textField_idComments, "cell 8 1,growx");
            	 textField_idComments.setColumns(10);
            	 
            	 JLabel lblAgentUtterance = new JLabel("agent utterance");
            	 frame.getContentPane().add(lblAgentUtterance, "cell 0 3,alignx center");
            	 
            	 
            	 addChangeListener(textArea_AU, e -> addToString("textArea_AU",textArea_AU.getText()));
            	 frame.getContentPane().add(textArea_AU, "cell 2 3 5 1,grow");
            	 
            	 JSeparator separator = new JSeparator();
            	 frame.getContentPane().add(separator, "cell 7 0 1 8");
            	 
            	 
            	 addChangeListener(textArea_AUcomments, e -> addToString("textArea_AUcomments",textArea_AUcomments.getText()));
            	 frame.getContentPane().add(textArea_AUcomments, "cell 8 3,grow");
            	 
            	 JLabel lblAgentBehaviour = new JLabel("agent behaviour");
            	 frame.getContentPane().add(lblAgentBehaviour, "cell 0 5,alignx center");
            	 
            	 
            	 addChangeListener(textArea_agentNVbehaviour, e -> addToString("textArea_agentNVbehaviour",textArea_agentNVbehaviour.getText()));
            	 frame.getContentPane().add(textArea_agentNVbehaviour, "cell 2 5 5 1,grow");
            	 
            	 
            	 addChangeListener(textArea_agentNVbehaviourComments, e -> addToString("textArea_agentNVbehaviourComments",textArea_agentNVbehaviourComments.getText()));
            	 frame.getContentPane().add(textArea_agentNVbehaviourComments, "cell 8 5,grow");
            	 
            	 JLabel lblUserUtterance = new JLabel("user utterance");
            	 frame.getContentPane().add(lblUserUtterance, "cell 0 7,alignx center");
            	 
            	 
            	 addChangeListener(textArea_UU, e -> addToString("textArea_UU",textArea_UU.getText()));
            	 frame.getContentPane().add(textArea_UU, "cell 2 7 5 1,grow");
            	 
            	 JTextArea textArea_UUcomments = new JTextArea();
            	 addChangeListener(textArea_UUcomments, e -> addToString("textArea_UUcomments",textArea_UUcomments.getText()));
            	 frame.getContentPane().add(textArea_UUcomments, "cell 8 7,grow");
            	 
            	 JLabel lblRules = new JLabel("rules");
            	 frame.getContentPane().add(lblRules, "cell 0 9,alignx center");
            	 
            	 
            	 addChangeListener(textArea_rules, e -> addToString("textArea_rules",textArea_rules.getText()));
            	 frame.getContentPane().add(textArea_rules, "cell 2 9 5 1,grow");
            	 
            	 JTextArea textArea_rulesComments = new JTextArea();
            	 addChangeListener(textArea_rulesComments, e -> addToString("textArea_rulesComments",textArea_rulesComments.getText()));
            	 frame.getContentPane().add(textArea_rulesComments, "cell 8 9,grow");
            	 
            	 JButton btnNewEpisode = new JButton("new episode");
            	 btnNewEpisode.addActionListener(new ActionListener() {
            	 	public void actionPerformed(ActionEvent arg0) {
            	 		writeMove();
            	 		textField_idEpisode.setText("");
            	 		textField_idExchange.setText("");
            	 		textField_idMove.setText("");
            	 		//ugh, clean all fields
            	 		textField_idComments.setText("");
            	 		textArea_AU.setText("");  
            	 		textArea_AUcomments.setText("");
            	 		textArea_agentNVbehaviour.setText("");
            	 		textArea_agentNVbehaviourComments.setText("");
            	 		textArea_UU.setText("");
            	 		textArea_UUcomments.setText("");
            	 		textArea_rules.setText("");
            	 		textArea_rulesComments.setText("");
            	 	}
            	 });
            	 frame.getContentPane().add(btnNewEpisode, "flowx,cell 2 10,alignx center");
            	 
            	 JButton btnNewExchange = new JButton("new exchange");
            	 btnNewExchange.addActionListener(new ActionListener() {
            	 	public void actionPerformed(ActionEvent e) {
            	 		writeMove();
            	 		textField_idExchange.setText("");
            	 		textField_idMove.setText("");  
            	 		//ugh, clean all fields
            	 		textField_idComments.setText("");
            	 		textArea_AU.setText("");  
            	 		textArea_AUcomments.setText("");
            	 		textArea_agentNVbehaviour.setText("");
            	 		textArea_agentNVbehaviourComments.setText("");
            	 		textArea_UU.setText("");
            	 		textArea_UUcomments.setText("");
            	 		textArea_rules.setText("");
            	 		textArea_rulesComments.setText("");
            	 	}
            	 });
            	 frame.getContentPane().add(btnNewExchange, "cell 4 10,alignx center");
            	 
            	 JButton btnNewMove = new JButton("new move");
            	 btnNewMove.addActionListener(new ActionListener() {
            	 	public void actionPerformed(ActionEvent e) {
            	 		writeMove();
            	 		textField_idMove.setText("");
            	 		//ugh, clean all fields
            	 		textField_idComments.setText("");
            	 		textArea_AU.setText("");  
            	 		textArea_AUcomments.setText("");
            	 		textArea_agentNVbehaviour.setText("");
            	 		textArea_agentNVbehaviourComments.setText("");
            	 		textArea_UU.setText("");
            	 		textArea_UUcomments.setText("");
            	 		textArea_rules.setText("");
            	 		textArea_rulesComments.setText("");
            	 	}
            	 });
            	 frame.getContentPane().add(btnNewMove, "cell 6 10,alignx center");
            	 
            	 JLabel lblType = new JLabel("Tag: ");
            	 frame.getContentPane().add(lblType, "flowx,cell 8 10");
            	 
            	 
            	 rdbtnC.setSelected(true);
            	 buttonGroup.add(rdbtnC);
            	 rdbtnC.addActionListener(new ActionListener(){
            		 public void actionPerformed(ActionEvent e) {
            			 moveTag = "C";            			 
            		 }
            	 });
            	 frame.getContentPane().add(rdbtnC, "cell 8 10");
            	 
            	 
            	 buttonGroup.add(rdbtnO);
            	 rdbtnO.addActionListener(new ActionListener(){
            		 public void actionPerformed(ActionEvent e) {
            			 moveTag = "O";            			 
            		 }
            	 });
            	 frame.getContentPane().add(rdbtnO, "cell 8 10");
            	 
            	 
            	 buttonGroup.add(rdbtnM);
            	 rdbtnM.addActionListener(new ActionListener(){
            		 public void actionPerformed(ActionEvent e) {
            			 moveTag = "M";            			 
            		 }
            	 });
            	 frame.getContentPane().add(rdbtnM, "cell 8 10");
            	 
            	 frame.setVisible(true);
            	 frame.setLocation(100, 50);
            	 frame.setMinimumSize(new Dimension(850, 400));
            	 
            	 JMenuBar menuBar = new JMenuBar();
            	 frame.setJMenuBar(menuBar);
            	 
            	 JMenu mnMenu = new JMenu("File");
            	 menuBar.add(mnMenu);
            	 
            	 JMenuItem mntmNewDialogue = new JMenuItem("New Dialogue");
            	 mntmNewDialogue.addMouseListener(new MouseAdapter() {
            	 	@Override
            	 	public void mouseReleased(MouseEvent arg0) {
            	 		//clean all fields
            	 		textField_idEpisode.setText("");
            	 		textField_idExchange.setText("");
            	 		textField_idMove.setText("");
            	 		textField_idComments.setText("");
            	 		textArea_AU.setText("");  
            	 		textArea_AUcomments.setText("");
            	 		textArea_agentNVbehaviour.setText("");
            	 		textArea_agentNVbehaviourComments.setText("");
            	 		textArea_UU.setText("");
            	 		textArea_UUcomments.setText("");
            	 		textArea_rules.setText("");
            	 		textArea_rulesComments.setText("");
            	 	}
            	 });
            	 mnMenu.add(mntmNewDialogue);
            	 
            	 JMenuItem mntmOpenDialogue = new JMenuItem("Open Dialogue");
            	 mntmOpenDialogue.addMouseListener(new MouseAdapter() {
            	 	@Override
            	 	public void mouseReleased(MouseEvent e) {
            	 		JFileChooser openFile = new JFileChooser();
                        openFile.showOpenDialog(null);
                        if(openFile.APPROVE_SELECTION != null){
	                        File file = openFile.getSelectedFile();
	                        System.out.println("File: "+file);
	                        readMove(file);
                        }
            	 	}
            	 });
            	 mnMenu.add(mntmOpenDialogue);
            	 
            	 JMenuItem mntmSaveDialogue = new JMenuItem("Save Dialogue");
            	 mntmSaveDialogue.addMouseListener(new MouseAdapter() {
            	 	@Override
            	 	public void mouseReleased(MouseEvent e) {
            	 		//we only want to save if we have a move name, otherwise it messes everything up later
            	 		try {
							if(!idEpisode.equals("") && !idExchange.equals("") && !idMove.equals("")){
								writeMove();
							}else JOptionPane.showMessageDialog(frame, "No Episode, Exchange, or Move 'id' defined.");
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(frame, "No Episode, Exchange, or Move 'id' defined.");
						}
            	 	}
            	 });
            	 mnMenu.add(mntmSaveDialogue);
            	 
            	 JMenuItem mntmExit = new JMenuItem("Exit");
            	 mntmExit.addMouseListener(new MouseAdapter() {
            	 	@Override
            	 	public void mouseReleased(MouseEvent e) {
            	 		System.exit(0);
            	 	}
            	 });
            	 mnMenu.add(mntmExit);
            	 
            	 JMenu mnType = new JMenu("Settings");
            	 menuBar.add(mnType);
            	 
            	 JMenu mnMoveType = new JMenu("Move Type");
            	 mnType.add(mnMoveType);
            	 
            	 JMenuItem mntmContentmove = new JMenuItem("Content-Move");
            	 mntmContentmove.addMouseListener(new MouseAdapter() {
            	 	@Override
            	 	public void mouseReleased(MouseEvent arg0) {
            	 	}
            	 });
            	 mnMoveType.add(mntmContentmove);
            	 
            	 JMenuItem mntmInteractionmove = new JMenuItem("Interaction-Move");
            	 mntmInteractionmove.addMouseListener(new MouseAdapter() {
            	 	@Override
            	 	public void mouseReleased(MouseEvent e) {
            	 	}
            	 });
            	 mnMoveType.add(mntmInteractionmove);
            	 
            	 JMenuItem mntmSocioemotionalmove = new JMenuItem("Emotional-Move");
            	 mntmSocioemotionalmove.addMouseListener(new MouseAdapter() {
            	 	@Override
            	 	public void mouseReleased(MouseEvent e) {
            	 	}
            	 });
            	 mnMoveType.add(mntmSocioemotionalmove);
            	 
            	 JMenu mnAgentuser = new JMenu("Agent/User");
            	 mnAgentuser.setEnabled(false);
            	 mnType.add(mnAgentuser);
            	 
            	 JMenuItem mntmAgent = new JMenuItem("Agent");
            	 mntmAgent.setEnabled(false);
            	 mnAgentuser.add(mntmAgent);
            	 
            	 JMenuItem mntmUser = new JMenuItem("User");
            	 mntmUser.setEnabled(false);
            	 mnAgentuser.add(mntmUser);
            }
        });
	}
	
	
	/**
	 * Installs a listener to receive notification when the text of any
	 * {@code JTextComponent} is changed. Internally, it installs a
	 * {@link DocumentListener} on the text component's {@link Document},
	 * and a {@link PropertyChangeListener} on the text component to detect
	 * if the {@code Document} itself is replaced.
	 * 
	 * @param text any text component, such as a {@link JTextField}
	 *        or {@link JTextArea}
	 * @param changeListener a listener to receive {@link ChangeEvent}s
	 *        when the text is changed; the source object for the events
	 *        will be the text component
	 * @throws NullPointerException if either parameter is null
	 */
	public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
	    Objects.requireNonNull(text);
	    Objects.requireNonNull(changeListener);
	    DocumentListener dl = new DocumentListener() {
	        private int lastChange = 0, lastNotifiedChange = 0;
			@Override
			public void changedUpdate(DocumentEvent e) {
			    lastChange++;
	            SwingUtilities.invokeLater(() -> {
	                if (lastNotifiedChange != lastChange) {
	                    lastNotifiedChange = lastChange;
	                    changeListener.stateChanged(new ChangeEvent(text));
	                }
	            });				
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
			    changedUpdate(e);				
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
			    changedUpdate(e);				
			}
	    };	    
	    text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
	        Document d1 = (Document)e.getOldValue();
	        Document d2 = (Document)e.getNewValue();
	        if (d1 != null) d1.removeDocumentListener(dl);
	        if (d2 != null) d2.addDocumentListener(dl);
	        dl.changedUpdate(null);
	    });
	    Document d = text.getDocument();
	    if (d != null) d.addDocumentListener(dl);
	}

}
