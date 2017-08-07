package eu.aria.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javax.swing.WindowConstants;
import vib.application.ariavaluspa.core.ARIAInformationStatesManager;
import vib.application.ariavaluspa.tools.ARIAInformationStatesSelectorForm;
import vib.auxiliary.activemq.gui.WhitboardFrame;
import vib.auxiliary.activemq.semaine.BMLCallbacksSender;
import vib.auxiliary.activemq.semaine.FMLReceiver;
import vib.auxiliary.player.ogre.OgreFrame;
import vib.auxiliary.tts.cereproc.CereprocTTS;
import vib.core.animation.lipmodel.LipModel;
import vib.core.animation.mpeg4.MPEG4Animatable;
import vib.core.animation.mpeg4.fap.filters.LipBlender;
import vib.core.animation.performer.AnimationKeyframePerformer;
import vib.core.animation.performer.BodyAnimationNoiseGenerator;
import vib.core.behaviorplanner.Planner;
import vib.core.behaviorrealizer.Realizer;
import vib.core.interruptions.InterruptionManager;
import vib.core.keyframes.AudioKeyFramePerformer;
import vib.core.keyframes.face.FaceKeyframePerformer;
import vib.core.keyframes.face.SimpleAUPerformer;
import vib.core.util.IniManager;
import vib.core.util.environment.Environment;
import vib.core.util.math.Quaternion;
import vib.core.utilx.gui.TTSController;



/**
 * Created by adg on 17/08/2016.
 * Modified by Angelo Cafaro on 19/06/2017
 *
 */
public class Greta {

    private static java.awt.Image icon = null;

    public void init(IniManager initManager) {
        try {
            updatePluginsCfg();
        } catch (IOException e) {
            System.out.println("WARNING: Failed to modify Plugins_OpenGL.cfg");
        }
        
        if ("living-actor".equalsIgnoreCase(initManager.getValueString("System.animEngine"))) {
            initLivingActor(initManager);
        } else {
            initGreta(initManager);
        }
    }
    
    private void initLivingActor(IniManager initManager) {
        // Init the Intention Planner
        Planner behaviorPlanner = new Planner();
        
         // Init the TTS
        TTSController tTS = new TTSController();
        tTS.setDoTemporize(true);
        tTS.setDoAudio(true);
        tTS.setDoPhonemes(true);
        tTS.dispose();
        
        // Instantiate the specific TTS (Cereproc)
        CereprocTTS cereproc_TTS = new CereprocTTS();
        
        // Creates the Interruption Manager
        //InterruptionManager interruptionManager = new InterruptionManager();
        
        // Create the ActiveMQ FML Receiver
        FMLReceiver amqFMLReceiver = new FMLReceiver();
        amqFMLReceiver.setHost(initManager.getValueString("FMLReceiver.host"));
        amqFMLReceiver.setPort(initManager.getValueString("FMLReceiver.port"));
        amqFMLReceiver.setTopic(initManager.getValueString("FMLReceiver.topic"));
        amqFMLReceiver.setIsQueue(initManager.getValueBoolean("FMLReceiver.isQueue"));
        WhitboardFrame amqFMLReceiverModuleFrame = new WhitboardFrame();
        amqFMLReceiverModuleFrame.setWhitboard(amqFMLReceiver);
        if(icon!=null){amqFMLReceiverModuleFrame.setIconImage(icon);}
        amqFMLReceiverModuleFrame.setTitle("FML Receiver");
        amqFMLReceiverModuleFrame.setLocation(1082, 10);
        amqFMLReceiverModuleFrame.setSize(260, 240);
        amqFMLReceiverModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqFMLReceiverModuleFrame.setVisible(true);
        
        // Create the ActiveMQ BML Sender
        vib.auxiliary.activemq.semaine.BMLSender amqBMLSender = new vib.auxiliary.activemq.semaine.BMLSender();
        amqBMLSender.setHost(initManager.getValueString("BMLSender.host"));
        amqBMLSender.setPort(initManager.getValueString("BMLSender.port"));
        amqBMLSender.setTopic(initManager.getValueString("BMLSender.topic"));
        amqBMLSender.setIsQueue(initManager.getValueBoolean("BMLSender.isQueue"));
        WhitboardFrame amqBMLSenderModuleFrame = new WhitboardFrame();
        amqBMLSenderModuleFrame.setWhitboard(amqBMLSender);
        if(icon!=null){amqBMLSenderModuleFrame.setIconImage(icon);}
        amqBMLSenderModuleFrame.setTitle("BML Sender");
        amqBMLSenderModuleFrame.setLocation(1382, 40);
        amqBMLSenderModuleFrame.setSize(260, 240);
        amqBMLSenderModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqBMLSenderModuleFrame.setVisible(true);
        
        // Create the ActiveMQ BML Feedback Receiver
        vib.auxiliary.activemq.semaine.BMLCallbacksReceiver amqBMLCallbacksReceiver = new vib.auxiliary.activemq.semaine.BMLCallbacksReceiver();
        amqBMLCallbacksReceiver.setHost(initManager.getValueString("BMLFeedbackReceiver.host"));
        amqBMLCallbacksReceiver.setPort(initManager.getValueString("BMLFeedbackReceiver.port"));
        amqBMLCallbacksReceiver.setTopic(initManager.getValueString("BMLFeedbackReceiver.topic"));
        amqBMLCallbacksReceiver.setIsQueue(initManager.getValueBoolean("BMLFeedbackReceiver.isQueue"));
        WhitboardFrame amqBMLCallbacksReceiverModuleFrame = new WhitboardFrame();
        amqBMLCallbacksReceiverModuleFrame.setWhitboard(amqBMLCallbacksReceiver);
        if(icon!=null){amqBMLCallbacksReceiverModuleFrame.setIconImage(icon);}
        amqBMLCallbacksReceiverModuleFrame.setTitle("BML Feedback Receiver");
        amqBMLCallbacksReceiverModuleFrame.setLocation(1382, 40);
        amqBMLCallbacksReceiverModuleFrame.setSize(260, 240);
        amqBMLCallbacksReceiverModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqBMLCallbacksReceiverModuleFrame.setVisible(true);
        
        // Connect the modules
        tTS.setTTS(cereproc_TTS);
        //behaviorPlanner.addSignalPerformer(bmlSenderThrift);
        behaviorPlanner.addSignalPerformer(amqBMLSender);
        //interruptionManager.addIntentionPerformer(behaviorPlanner);
        //amqFMLReceiver.addIntentionPerformer(interruptionManager);
        //amqBMLCallbacksReceiver.addCallbackPerformer(interruptionManager);
    }

    private void initGreta(IniManager iniManager) {
        // Init the Intention Planner
        Planner behaviorPlanner = new Planner();

        // Init the BML Realizer
        Realizer behaviorRealizer = new Realizer();

        // Init the Face Key Frame Peformer
        FaceKeyframePerformer faceKeyframePerformer = new FaceKeyframePerformer();
        faceKeyframePerformer.setBlinking(false);

        // Init the Lip Model
        LipModel lipModel = new LipModel();

        // Init the Simple AUs Peformer
        SimpleAUPerformer simpleAUPerformer = new SimpleAUPerformer();

        // Init the Lip Blender
        LipBlender lipBlender = new LipBlender();

        // Init the Audio Key Frame Peformer
        AudioKeyFramePerformer audioKeyframePerformer = new AudioKeyFramePerformer();

        // Init the Environment Manager
        Environment environment = new Environment(iniManager.getValueString("Environment"));

        // Init the Ogre3D Player
        OgreFrame playerOgre = new OgreFrame();
        playerOgre.setCameraPositionX(iniManager.getValueDouble("Camera.posX"));
        playerOgre.setCameraPositionY(iniManager.getValueDouble("Camera.posY"));
        playerOgre.setCameraPositionZ(iniManager.getValueDouble("Camera.posZ"));
        playerOgre.setCameraPitch(Math.toRadians(iniManager.getValueDouble("Camera.pitch")));
        playerOgre.setCameraYaw(Math.toRadians(iniManager.getValueDouble("Camera.yaw")));
        playerOgre.setCameraRoll(Math.toRadians(iniManager.getValueDouble("Camera.roll")));
        if(icon!=null){playerOgre.setIconImage(icon);}
        playerOgre.setTitle("PlayerOgre");
        playerOgre.setLocation(iniManager.getValueInt("Player.window.x"), iniManager.getValueInt("Player.window.y"));
        playerOgre.setSize(iniManager.getValueInt("Player.window.width"), iniManager.getValueInt("Player.window.height"));
        playerOgre.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        playerOgre.setVisible(true);

        // Init the TTS
        TTSController tTS = new TTSController();
        tTS.setDoTemporize(true);
        tTS.setDoAudio(true);
        tTS.setDoPhonemes(true);
        tTS.dispose();

        // Instantiate the specific TTS (Cereproc)
        CereprocTTS cereproc_TTS = new CereprocTTS();

        // Init the Mpeg4 Animation Table
        MPEG4Animatable mPEG4Animatable = new MPEG4Animatable();     
        mPEG4Animatable.setCoordinateX(iniManager.getValueDouble("Agent.posX"));
        mPEG4Animatable.setCoordinateY(iniManager.getValueDouble("Agent.posY"));
        mPEG4Animatable.setCoordinateZ(iniManager.getValueDouble("Agent.posZ"));
        
        Quaternion agentOri = Quaternion.fromXYZInDegrees(iniManager.getValueDouble("Agent.rotX"), 
                                                          iniManager.getValueDouble("Agent.rotY"),
                                                          iniManager.getValueDouble("Agent.rotZ"));
        
        mPEG4Animatable.setOrientation(agentOri);
        mPEG4Animatable.setScaleX(iniManager.getValueDouble("Agent.scaleX"));
        mPEG4Animatable.setScaleY(iniManager.getValueDouble("Agent.scaleY"));
        mPEG4Animatable.setScaleZ(iniManager.getValueDouble("Agent.scaleZ"));

        // Init the Animation Key Frame Perfomer
        AnimationKeyframePerformer animationKeyframePerformer = new AnimationKeyframePerformer();
        animationKeyframePerformer.setUsePropagation(true);
        animationKeyframePerformer.setWeightPropagation(0.2);

        // Creates the Interruption Manager
        InterruptionManager interruptionManager = new InterruptionManager();
        
        // Creates the ARIA Information States Manager
        ARIAInformationStatesManager ariaInfoStateManager = new ARIAInformationStatesManager();
        
        // Creates the ARIA Information States Tester
        ARIAInformationStatesSelectorForm ariaInfoStateManagerForm = new ARIAInformationStatesSelectorForm();
        if(icon!=null){ariaInfoStateManagerForm.setIconImage(icon);}
        ariaInfoStateManagerForm.setTitle("ARIA Information State Changer");
        ariaInfoStateManagerForm.setLocation(400, 600);
        ariaInfoStateManagerForm.setSize(400, 150);
        ariaInfoStateManagerForm.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        ariaInfoStateManagerForm.setVisible(iniManager.getValueBoolean("ARIAInformationStateTester.visible"));
        
        // Create the ActiveMQ FML Receiver
        FMLReceiver amqFMLReceiver = new FMLReceiver();
        amqFMLReceiver.setHost(iniManager.getValueString("FMLReceiver.host"));
        amqFMLReceiver.setPort(iniManager.getValueString("FMLReceiver.port"));
        amqFMLReceiver.setTopic(iniManager.getValueString("FMLReceiver.topic"));
        amqFMLReceiver.setIsQueue(iniManager.getValueBoolean("FMLReceiver.isQueue"));
        WhitboardFrame amqFMLReceiverModuleFrame = new WhitboardFrame();
        amqFMLReceiverModuleFrame.setWhitboard(amqFMLReceiver);
        if(icon!=null){amqFMLReceiverModuleFrame.setIconImage(icon);}
        amqFMLReceiverModuleFrame.setTitle("FML Receiver");
        amqFMLReceiverModuleFrame.setLocation(1082, 10);
        amqFMLReceiverModuleFrame.setSize(260, 240);
        amqFMLReceiverModuleFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        amqFMLReceiverModuleFrame.setVisible(iniManager.getValueBoolean("WhiteBoard.visible"));

        // Create the ActiveMQ BML Feedback Sender
        BMLCallbacksSender amqBMLCallbacksSender = new BMLCallbacksSender();
        amqBMLCallbacksSender.setHost(iniManager.getValueString("BMLFeedbackSender.host"));
        amqBMLCallbacksSender.setPort(iniManager.getValueString("BMLFeedbackSender.port"));
        amqBMLCallbacksSender.setTopic(iniManager.getValueString("BMLFeedbackSender.topic"));
        amqBMLCallbacksSender.setIsQueue(iniManager.getValueBoolean("BMLFeedbackSender.isQueue"));
        WhitboardFrame amqBMLCallbacksSenderModuleFrame = new WhitboardFrame();
        amqBMLCallbacksSenderModuleFrame.setWhitboard(amqBMLCallbacksSender);
        if(icon!=null){amqBMLCallbacksSenderModuleFrame.setIconImage(icon);}
        amqBMLCallbacksSenderModuleFrame.setTitle("BML Feedback Sender");
        amqBMLCallbacksSenderModuleFrame.setLocation(1382, 10);
        amqBMLCallbacksSenderModuleFrame.setSize(260, 240);
        amqBMLCallbacksSenderModuleFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        amqBMLCallbacksSenderModuleFrame.setVisible(iniManager.getValueBoolean("WhiteBoard.visible"));

        // Create the ActiveMQ BML Sender
        vib.auxiliary.activemq.semaine.BMLSender amqBMLSender = new vib.auxiliary.activemq.semaine.BMLSender();
        amqBMLSender.setHost(iniManager.getValueString("BMLSender.host"));
        amqBMLSender.setPort(iniManager.getValueString("BMLSender.port"));
        amqBMLSender.setTopic(iniManager.getValueString("BMLSender.topic"));
        amqBMLSender.setIsQueue(iniManager.getValueBoolean("BMLSender.isQueue"));
        WhitboardFrame amqBMLSenderModuleFrame = new WhitboardFrame();
        amqBMLSenderModuleFrame.setWhitboard(amqBMLSender);
        if(icon!=null){amqBMLSenderModuleFrame.setIconImage(icon);}
        amqBMLSenderModuleFrame.setTitle("BML Sender");
        amqBMLSenderModuleFrame.setLocation(1382, 40);
        amqBMLSenderModuleFrame.setSize(260, 240);
        amqBMLSenderModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqBMLSenderModuleFrame.setVisible(iniManager.getValueBoolean("WhiteBoard.visible"));
        
        // Create the ActiveMQ BML Receiver
        vib.auxiliary.activemq.semaine.BMLReceiver amqBMLReceiver = new vib.auxiliary.activemq.semaine.BMLReceiver();
        amqBMLReceiver.setHost(iniManager.getValueString("BMLReceiver.host"));
        amqBMLReceiver.setPort(iniManager.getValueString("BMLReceiver.port"));
        amqBMLReceiver.setTopic(iniManager.getValueString("BMLReceiver.topic"));
        amqBMLReceiver.setIsQueue(iniManager.getValueBoolean("BMLReceiver.isQueue"));
        WhitboardFrame amqBMLReceiverModuleFrame = new WhitboardFrame();
        amqBMLReceiverModuleFrame.setWhitboard(amqBMLReceiver);
        if(icon!=null){amqBMLReceiverModuleFrame.setIconImage(icon);}
        amqBMLReceiverModuleFrame.setTitle("BML Receiver");
        amqBMLReceiverModuleFrame.setLocation(1382, 80);
        amqBMLReceiverModuleFrame.setSize(260, 240);
        amqBMLReceiverModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqBMLReceiverModuleFrame.setVisible(iniManager.getValueBoolean("WhiteBoard.visible"));
        
        // Create the ActiveMQ Information States Receiver
        vib.auxiliary.activemq.aria.ARIAInformationStateReceiver amqARIAInfoStateReceiver = new vib.auxiliary.activemq.aria.ARIAInformationStateReceiver();
        amqARIAInfoStateReceiver.setHost(iniManager.getValueString("ARIAInfoStateReceiver.host"));
        amqARIAInfoStateReceiver.setPort(iniManager.getValueString("ARIAInfoStateReceiver.port"));
        amqARIAInfoStateReceiver.setTopic(iniManager.getValueString("ARIAInfoStateReceiver.topic"));
        amqARIAInfoStateReceiver.setIsQueue(iniManager.getValueBoolean("ARIAInfoStateReceiver.isQueue"));
        WhitboardFrame amqARIAInfoStateReceiverModuleFrame = new WhitboardFrame();
        amqARIAInfoStateReceiverModuleFrame.setWhitboard(amqARIAInfoStateReceiver);
        if(icon!=null){amqBMLReceiverModuleFrame.setIconImage(icon);}
        amqARIAInfoStateReceiverModuleFrame.setTitle("ARIA Information State Receiver");
        amqARIAInfoStateReceiverModuleFrame.setLocation(900, 100);
        amqARIAInfoStateReceiverModuleFrame.setSize(260, 240);
        amqARIAInfoStateReceiverModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqARIAInfoStateReceiverModuleFrame.setVisible(false);
        
        // Create a body animation noise generator
        BodyAnimationNoiseGenerator bodyNoiseGenerator = new BodyAnimationNoiseGenerator();
        bodyNoiseGenerator.setUseLowerBody(false);
        bodyNoiseGenerator.setUseTorso(true);
        bodyNoiseGenerator.setUseHead(true);
        bodyNoiseGenerator.setIntensityHead(0.7);
        bodyNoiseGenerator.setIntensityTorso(0.6);

        // Connect the modules
        simpleAUPerformer.addFAPFramePerformer(lipBlender);
        animationKeyframePerformer.addBAPFramesPerformer(mPEG4Animatable);
        environment.addNode(mPEG4Animatable);
        behaviorRealizer.addKeyframePerformer(faceKeyframePerformer);
        behaviorRealizer.addKeyframePerformer(lipModel);
        behaviorRealizer.addKeyframePerformer(audioKeyframePerformer);
        behaviorRealizer.addKeyframePerformer(animationKeyframePerformer);
        lipBlender.addFAPFramePerformer(mPEG4Animatable);
        lipBlender.setLipSource(lipModel);
        playerOgre.setEnvironment(environment);
        faceKeyframePerformer.addAUPerformer(simpleAUPerformer);
        tTS.setTTS(cereproc_TTS);
        audioKeyframePerformer.addAudioPerformer(mPEG4Animatable);
        behaviorPlanner.addSignalPerformer(behaviorRealizer);
        behaviorPlanner.addSignalPerformer(amqBMLSender);
        bodyNoiseGenerator.addBAPFramesPerformer(mPEG4Animatable);
        amqFMLReceiver.addIntentionPerformer(interruptionManager);
        behaviorRealizer.addCallbackPerformer(amqBMLCallbacksSender);
        behaviorRealizer.addCallbackPerformer(interruptionManager);
        interruptionManager.addIntentionPerformer(behaviorPlanner);
        behaviorRealizer.setEnvironment(environment);
        interruptionManager.addSignalPerformer(behaviorRealizer);
        ariaInfoStateManager.setEnvironment(environment);
        ariaInfoStateManager.addSignalPerformer(behaviorRealizer);
        ariaInfoStateManagerForm.setARIAInformationStateManager(ariaInfoStateManager);
        amqARIAInfoStateReceiver.addARIAInformationStatePerformer(ariaInfoStateManager);
    }
    
    private static boolean updatePluginsCfg() throws IOException {
        final String HEADER = "PluginFolder=";
        final String FILE = "Player\\Lib\\External\\Win64\\Plugins_OpenGL.cfg";
        final String DLL_PATH = "\\Player\\Lib\\External\\Win64\\DLL";

        File file = new File(FILE);
        File fileBk = new File(file.getAbsolutePath() + ".bk");

        /* if (fileBk.exists()) {
            System.out.println("Plugins_OpenGL.cfg already updated.");
            return false;
        }*/

        String cwd = Paths.get(".").toAbsolutePath().normalize().toString();
        String dll_path = HEADER + cwd + DLL_PATH;

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
                if (line.startsWith(HEADER)) {
                    if (line.equals(dll_path)) {
                        // no need to rewrite the file
                        System.out.println("Plugins_OpenGL.cfg already updated.");
                        return false;
                    }
                }
            }
        }

        Files.copy(file.toPath(), fileBk.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (String line : lines) {
                if (line.startsWith(HEADER)) {
                    // f.write('PluginFolder=' + join(os.getcwd(), filepath, "DLL") + "\n")
                    bw.write(dll_path);
                    bw.write("\n");
                } else {
                    bw.write(line);
                    bw.write("\n");
                }
            }
        }

        System.out.println("Plugins_OpenGL.cfg updated successfully.");

        return true;
    }
}
