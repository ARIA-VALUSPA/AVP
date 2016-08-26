/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aria.core;

import eu.aria.core.DM.DialogueManager;
import eu.aria.core.activemq.InputConnection;
import eu.aria.core.activemq.OutputConnection;
import eu.aria.dialogue.dm.DMPool;
import eu.aria.dialogue.main.DialogueManagerGUI;
import vib.core.util.IniManager;

/**
 *
 * @author adg
 */
public class Main {

    private static void runCore(Config config) {
        IniManager iniManager = new IniManager("./Agent-Core.ini");

        // Try to setup of the appearance of the GUI to the OS one:
        try {javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());} catch (Exception e) {}

        DialogueManagerGUI.setResourcesFolder(".\\DM-place-holder\\Resources");
        DialogueManagerGUI.setResultsFolder(".\\DM-place-holder\\Results");

        DialogueManagerGUI dialogueManagerGUI = new DialogueManagerGUI(1);
        DialogueManager dialogueManager = new DialogueManager();
        InputConnection inputBox = new InputConnection();
        OutputConnection outputBox = new OutputConnection();

        DMPool.getInstance().setOhTime(iniManager.getValueInt("DM.singleWordDelay"));
        DMPool.getInstance().setPromptUserTime(iniManager.getValueInt("DM.idleTime"));
        dialogueManager.addFmlListener(outputBox::sendFML);
        dialogueManager.setUseAsrActive(iniManager.getValueBoolean("DM.useAsrActive"));

        inputBox.addAGenderListener(dialogueManager.getAGenderListener());
        inputBox.addEMaxListener(dialogueManager.getEMaxListener());
        inputBox.addASRListener(dialogueManager.getASRListener());
        inputBox.addAudioEmotionListener(dialogueManager.getAudioEmotionListener());

        inputBox.init(config, iniManager);
        outputBox.init(config, iniManager);
        dialogueManager.init();
        dialogueManagerGUI.show();

        // This keeps the program alive to prevent the situation where there would be no frame or non-threads
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String input="";
        while(!input.equalsIgnoreCase("exit") && !input.equalsIgnoreCase("quit") && !input.equalsIgnoreCase("end")) {
            input = scanner.next();
        }
        System.out.println("Exiting ARIA-Core.");
        System.exit(0);
    }

    /*private static void fullSystem(Config config) {
        IniManager iniManager = new IniManager("./Agent-Core.ini");

        // Try to setup of the appearance of the GUI to the OS one:
        try {javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());} catch (Exception e) {}

        DialogueManagerGUI.setResourcesFolder(".\\DM-place-holder\\Resources");
        DialogueManagerGUI.setResultsFolder(".\\DM-place-holder\\Results");
        DialogueManagerGUI dialogueManagerGUI = new DialogueManagerGUI(1);
        Greta greta = new Greta();
        DialogueManager dialogueManager = new DialogueManager();
        InputConnection inputBox = new InputConnection();

        DMPool.getInstance().setOhTime(iniManager.getValueInt("DM.singleWordDelay"));
        DMPool.getInstance().setPromptUserTime(iniManager.getValueInt("DM.idleTime"));
        dialogueManager.addFmlListener(text -> greta.perform(text, "TempId"));
        dialogueManager.setUseAsrActive(iniManager.getValueBoolean("DM.useAsrActive"));
        inputBox.addAGenderListener(dialogueManager.getAGenderListener());
        inputBox.addEMaxListener(dialogueManager.getEMaxListener());
        inputBox.addASRListener(dialogueManager.getASRListener());
        inputBox.addAudioEmotionListener(dialogueManager.getAudioEmotionListener());

        inputBox.init(config);
        greta.setAgentReadyListener(dialogueManager::onAgentReady);
        greta.init();
        dialogueManager.init();
        dialogueManagerGUI.show();

        // This keeps the program alive to prevent the situation where there would be no frame or non-threads
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String input="";
        while(!input.equalsIgnoreCase("exit") && !input.equalsIgnoreCase("quit") && !input.equalsIgnoreCase("end")) {
            input = scanner.next();
        }
        System.out.println("Exiting ARIA-Core.");
        System.exit(0);
    }

    private static void eMaxMimic(Config config) {
        Greta greta = new Greta();
        InputConnection inputBox = new InputConnection();
        EMaxThresholdDialog eMaxThresholdDialog = new EMaxThresholdDialog();
        InputDialog inputDialog = new InputDialog();
        EMaxFilter eMaxFilter = new EMaxFilter(eMaxThresholdDialog);

        eMaxThresholdDialog.addEmotionFile(EMaxThresholdDialog.ANGE, "Common\\Data\\Examples-FML\\Examples Emotions\\Anger.xml");
        eMaxThresholdDialog.addEmotionFile(EMaxThresholdDialog.DISG, "Common\\Data\\Examples-FML\\Examples Emotions\\Bored.xml");
        eMaxThresholdDialog.addEmotionFile(EMaxThresholdDialog.FEAR, "Common\\Data\\Examples-FML\\Examples Emotions\\Disgust.xml");
        eMaxThresholdDialog.addEmotionFile(EMaxThresholdDialog.HAPP, "Common\\Data\\Examples-FML\\Examples Emotions\\Joy.xml");
        eMaxThresholdDialog.addEmotionFile(EMaxThresholdDialog.SADN, "Common\\Data\\Examples-FML\\Examples Emotions\\Sadness.xml");
        eMaxThresholdDialog.addEmotionFile(EMaxThresholdDialog.SURP, "Common\\Data\\Examples-FML\\Examples Emotions\\Surprise.xml");

        inputBox.addEMaxListener(eMaxThresholdDialog::onEMaxData);
        inputBox.addEMaxListener(eMaxFilter::onEMaxData);
        eMaxThresholdDialog.addIntentionPerformer(greta.getIntentionPerformer());
        eMaxFilter.setListener(inputDialog::setText);

        inputBox.init(config);
        greta.init();

        eMaxThresholdDialog.showWindow();
        inputDialog.showWindow();

        // This keeps the program alive to prevent the situation where there would be no frame or non-threads
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String input="";
        while(!input.equalsIgnoreCase("exit") && !input.equalsIgnoreCase("quit") && !input.equalsIgnoreCase("end")) {
            input = scanner.next();
        }
        System.out.println("Exiting ARIA-Core.");
        System.exit(0);
    }

    private static void dialogueManager() {
        IniManager iniManager = new IniManager("./Agent-Core.ini");

        DialogueManagerGUI.setResourcesFolder(".\\DM-place-holder\\Resources");
        DialogueManagerGUI.setResultsFolder(".\\DM-place-holder\\Results");
        DMPool.getInstance().setOhTime(iniManager.getValueInt("DM.singleWordDelay"));
        DMPool.getInstance().setPromptUserTime(iniManager.getValueInt("DM.idleTime"));

        DialogueManagerGUI aliceInteractiveGUI = new DialogueManagerGUI(1);
        aliceInteractiveGUI.showLog(false);
        aliceInteractiveGUI.show();
    }*/

    public static void main(String[] args) {
        Config.Builder configBuilder = new Config.Builder();

        for (String arg : args) {
            switch (arg) {
                case "-ssiWindows":
                    configBuilder.withSSIWindows();
                    break;
                case "-agentWindows":
                    configBuilder.withAgentWindows();
                    break;
            }
        }

        Config myConfig = configBuilder.build();

        runCore(myConfig);
    }
}