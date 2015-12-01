/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aria.core;

import eu.aria.alice.interactive.AliceInteractive;
import eu.aria.alice.interactive.AliceInteractiveGUI;
import eu.aria.core.DM.DialogueManager;
import eu.aria.core.activemq.InputFusionBox;
import eu.aria.core.demo.EMaxFilter;
import eu.aria.core.demo.EMaxThresholdDialog;
import eu.aria.core.demo.InputDialog;
import eu.aria.core.greta.Greta;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author adg
 */
public class Main {

    private static String lastASRSpeech = null;

    private static void fullSystem(Config config) {
        // Try to setup of the appearance of the GUI to the OS one:
        try {javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());} catch (Exception e) {}

        AliceInteractiveGUI.setDataFolder(".\\DM-place-holder\\Data");
        AliceInteractiveGUI.setResultsFolder(".\\DM-place-holder\\Results");
        AliceInteractiveGUI aliceInteractiveGUI = new AliceInteractiveGUI();
        Greta greta = new Greta();
        DialogueManager dialogueManager = new DialogueManager();
        InputFusionBox inputBox = new InputFusionBox();

        aliceInteractiveGUI.addListener(dialogueManager::userSays);
        dialogueManager.addPlainListener(aliceInteractiveGUI::agentSays);
        dialogueManager.addFmlListener(text -> greta.perform(text, "TempId"));
        inputBox.addAGenderListener(dialogueManager.getAGenderListener());
        inputBox.addEMaxListener(dialogueManager.getEMaxListener());
        inputBox.addASRListener(dialogueManager.getASRListener());
        inputBox.addASRListener(data -> {
            if (data != null && data.isActive() && (lastASRSpeech == null || !lastASRSpeech.equals(data.getSpeech()))) {
                aliceInteractiveGUI.agentSays("<font color=\"blue\">" + data.getSpeech() + "</font>");
                lastASRSpeech = data.getSpeech();
            }
        });

        inputBox.init(config);
        greta.init();
        dialogueManager.init();
        aliceInteractiveGUI.show();

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
        InputFusionBox inputBox = new InputFusionBox();
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

        inputBox.init(config, false, true, false);
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
        AliceInteractiveGUI.setDataFolder(".\\DM-place-holder\\Data");
        AliceInteractiveGUI.setResultsFolder(".\\DM-place-holder\\Results");
        AliceInteractiveGUI aliceInteractiveGUI = new AliceInteractiveGUI(new AliceInteractive());
        aliceInteractiveGUI.showLog(false);
        aliceInteractiveGUI.show();
    }

    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);
        Config.Builder configBuilder = new Config.Builder();

        for (String arg : args) {
            switch (arg) {
                case "-ssiWindows":
                    configBuilder.withSSIWindows();
                    break;
            }
        }

        Config myConfig = configBuilder.build();

        if (argList.contains("-all")) {
            fullSystem(myConfig);
        } else if (argList.contains("-emaxmimic")) {
            eMaxMimic(myConfig);
        } else if (argList.contains("-dmonly")) {
            dialogueManager();
        }
    }
}