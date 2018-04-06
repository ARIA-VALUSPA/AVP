package eu.aria.output;

import aria.vibtolivingactor.main.ThriftClient;
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
import vib.application.ariavaluspa.attitudes.ARIASocialAttitudesPlanner;
import vib.application.ariavaluspa.core.ARIAInformationStatesManager;
import vib.application.ariavaluspa.tools.ARIAInformationStatesSelectorForm;
import vib.application.ariavaluspa.tools.AriaValuspaFMLTranslator;
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
import vib.core.utilx.gui.DocOutput;
import vib.core.utilx.gui.DocOutputFrame;
import vib.core.utilx.gui.LogsController;
import vib.core.utilx.gui.TTSController;
import vib.auxiliary.activemq.semaine.BMLSender;
import vib.auxiliary.activemq.semaine.BMLCallbacksReceiver;
import vib.auxiliary.activemq.semaine.BMLReceiver;
import vib.auxiliary.activemq.aria.ARIAInformationStateReceiver;
import vib.auxiliary.player.ogre.capture.Capturecontroller;
import vib.auxiliary.thrift.gui.ThriftFrame;
import vib.core.intentions.FMLFileReader;
import vib.core.interruptions.InterruptionTester;
import vib.core.utilx.gui.CharacterIniManagerFrame;
import vib.core.utilx.gui.OpenAndLoad;
import vib.tools.ogre.capture.video.CodecSelector;
import vib.tools.ogre.capture.video.XuggleVideoCapture;

/**
 * Created by adg on 17/08/2016.
 * Modified by Angelo Cafaro
 *
 */
public class Greta {

    private static java.awt.Image icon = null;
    private static Planner behaviorPlanner;
    private static TTSController tTS;
    private static CereprocTTS cereproc_TTS;
    private static InterruptionManager interruptionManager;
    private static InterruptionTester interruptionTester;
    private static FMLReceiver amqFMLReceiver;
    private static WhitboardFrame amqFMLReceiverModuleFrame;
    private static BMLSender amqBMLSender;
    private static WhitboardFrame amqBMLSenderModuleFrame;
    private static BMLCallbacksReceiver amqBMLCallbacksReceiver;
    private static WhitboardFrame amqBMLCallbacksReceiverModuleFrame;
    private static DocOutput logFrame;
    private static DocOutputFrame logFrame_ModuleFrame;
    private static LogsController logs;
    private static Realizer behaviorRealizer;
    private static FaceKeyframePerformer faceKeyframePerformer;
    private static LipModel lipModel;
    private static SimpleAUPerformer simpleAUPerformer;
    private static LipBlender lipBlender;
    private static AudioKeyFramePerformer audioKeyframePerformer;
    private static Environment environment;
    private static OgreFrame playerOgre;
    private static MPEG4Animatable mPEG4Animatable;
    private static AnimationKeyframePerformer animationKeyframePerformer;
    private static ARIAInformationStatesManager ariaInfoStateManager;
    private static ARIAInformationStatesSelectorForm ariaInfoStateManagerForm;
    private static BMLCallbacksSender amqBMLCallbacksSender;
    private static WhitboardFrame amqBMLCallbacksSenderModuleFrame;
    private static BMLReceiver amqBMLReceiver;
    private static WhitboardFrame amqBMLReceiverModuleFrame;
    private static ARIAInformationStateReceiver amqARIAInfoStateReceiver;
    private static WhitboardFrame amqARIAInfoStateReceiverModuleFrame;
    private static BodyAnimationNoiseGenerator bodyNoiseGenerator;
    private static Capturecontroller captureController;
    private static XuggleVideoCapture xuggleVideoCapture;
    private static CodecSelector CodecSelectorFrame;
    private static ARIASocialAttitudesPlanner ariaSocialAttitudesPlanner;
    private static vib.auxiliary.thrift.BMLSender bmlSenderThrift;
    private static ThriftFrame bmlSenderThrift_ModuleFrame;            
    private static AriaValuspaFMLTranslator ariaValuspaFMLTranslator;
    private static CharacterIniManagerFrame characterManager;

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
        behaviorPlanner = new Planner();
        
         // Init the TTS
        tTS = new TTSController();
        tTS.setDoTemporize(true);
        tTS.setDoAudio(true);
        tTS.setDoPhonemes(true);
        tTS.dispose();
        
        // Instantiate the specific TTS (Cereproc)
        cereproc_TTS = new CereprocTTS();
        
        // Create the ActiveMQ FML Receiver
        amqFMLReceiver = new FMLReceiver();
        amqFMLReceiver.setHost(initManager.getValueString("FMLReceiver.host"));
        amqFMLReceiver.setPort(initManager.getValueString("FMLReceiver.port"));
        amqFMLReceiver.setTopic(initManager.getValueString("FMLReceiver.topic"));
        amqFMLReceiver.setIsQueue(initManager.getValueBoolean("FMLReceiver.isQueue"));
        amqFMLReceiverModuleFrame = new WhitboardFrame();
        amqFMLReceiverModuleFrame.setWhitboard(amqFMLReceiver);
        if(icon!=null){amqFMLReceiverModuleFrame.setIconImage(icon);}
        amqFMLReceiverModuleFrame.setTitle("FML Receiver");
        amqFMLReceiverModuleFrame.setLocation(1082, 10);
        amqFMLReceiverModuleFrame.setSize(260, 240);
        amqFMLReceiverModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqFMLReceiverModuleFrame.setVisible(true);
        
        // Create the ActiveMQ BML Sender
        amqBMLSender = new vib.auxiliary.activemq.semaine.BMLSender();
        amqBMLSender.setHost(initManager.getValueString("BMLSender.host"));
        amqBMLSender.setPort(initManager.getValueString("BMLSender.port"));
        amqBMLSender.setTopic(initManager.getValueString("BMLSender.topic"));
        amqBMLSender.setIsQueue(initManager.getValueBoolean("BMLSender.isQueue"));
        amqBMLSenderModuleFrame = new WhitboardFrame();
        amqBMLSenderModuleFrame.setWhitboard(amqBMLSender);
        if(icon!=null){amqBMLSenderModuleFrame.setIconImage(icon);}
        amqBMLSenderModuleFrame.setTitle("BML Sender");
        amqBMLSenderModuleFrame.setLocation(1382, 40);
        amqBMLSenderModuleFrame.setSize(260, 240);
        amqBMLSenderModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqBMLSenderModuleFrame.setVisible(true);
        
        // Create the ActiveMQ BML Feedback Receiver
        amqBMLCallbacksReceiver = new vib.auxiliary.activemq.semaine.BMLCallbacksReceiver();
        amqBMLCallbacksReceiver.setHost(initManager.getValueString("BMLFeedbackReceiver.host"));
        amqBMLCallbacksReceiver.setPort(initManager.getValueString("BMLFeedbackReceiver.port"));
        amqBMLCallbacksReceiver.setTopic(initManager.getValueString("BMLFeedbackReceiver.topic"));
        amqBMLCallbacksReceiver.setIsQueue(initManager.getValueBoolean("BMLFeedbackReceiver.isQueue"));
        amqBMLCallbacksReceiverModuleFrame = new WhitboardFrame();
        amqBMLCallbacksReceiverModuleFrame.setWhitboard(amqBMLCallbacksReceiver);
        if(icon!=null){amqBMLCallbacksReceiverModuleFrame.setIconImage(icon);}
        amqBMLCallbacksReceiverModuleFrame.setTitle("BML Feedback Receiver");
        amqBMLCallbacksReceiverModuleFrame.setLocation(1382, 40);
        amqBMLCallbacksReceiverModuleFrame.setSize(260, 240);
        amqBMLCallbacksReceiverModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqBMLCallbacksReceiverModuleFrame.setVisible(true);
        
        // Connect the modules
        tTS.setTTS(cereproc_TTS);
        behaviorPlanner.addSignalPerformer(amqBMLSender);       
        
        if (!initManager.getValueBoolean("System.livingactor.useActiveMQ")) {
            // Creates the Thrift BML Sender
            bmlSenderThrift = new vib.auxiliary.thrift.BMLSender(initManager.getValueString("ThriftBMLSender.host"), initManager.getValueInt("ThriftBMLSender.port"));
            bmlSenderThrift_ModuleFrame = new ThriftFrame();
            bmlSenderThrift_ModuleFrame.setConnector(bmlSenderThrift);
            if(icon!=null){bmlSenderThrift_ModuleFrame.setIconImage(icon);}
            bmlSenderThrift_ModuleFrame.setTitle("BMLSenderThrift");
            bmlSenderThrift_ModuleFrame.setLocation(400, 120);
            bmlSenderThrift_ModuleFrame.setSize(357, 166);
            bmlSenderThrift_ModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            bmlSenderThrift_ModuleFrame.setVisible(true);

            behaviorPlanner.addSignalPerformer(bmlSenderThrift);

            // Lunching the VIBLivingActorConnector
            ThriftClient.lunchVIBLivingActorConnector();
            
        }
    }

    private void initGreta(IniManager iniManager) {
        
        // Instantiate the modules
        logFrame = new DocOutput();
        logFrame_ModuleFrame = new DocOutputFrame();
        logFrame_ModuleFrame.setDocOutput(logFrame);
        logFrame_ModuleFrame.setBlack(true);
        if(icon!=null){logFrame_ModuleFrame.setIconImage(icon);}
        logFrame_ModuleFrame.setTitle("LogFrame");
        logFrame_ModuleFrame.setLocation(10, 560);
        logFrame_ModuleFrame.setSize(626, 373);
        logFrame_ModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        logFrame_ModuleFrame.setVisible(iniManager.getValueBoolean("Greta.LogWindow.visible"));

        // Init the Logs
        logs = new LogsController();
        logs.setDebug(iniManager.getValueBoolean("Greta.LogLevel.debug"));
        logs.setInfo(iniManager.getValueBoolean("Greta.LogLevel.info"));
        logs.setWarning(iniManager.getValueBoolean("Greta.LogLevel.warning"));
        logs.setError(iniManager.getValueBoolean("Greta.LogLevel.error"));
        
        logs.setTitle("Logs");
        logs.setLocation(900, 600);
        logs.setSize(212, 173);
        logs.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        logs.setVisible(iniManager.getValueBoolean("Greta.LogWindow.visible"));
        
        // Init the Intention Planner
        behaviorPlanner = new Planner();

        // Init the BML Realizer
        behaviorRealizer = new Realizer();

        // Init the Face Key Frame Peformer
        faceKeyframePerformer = new FaceKeyframePerformer();
        faceKeyframePerformer.setBlinking(iniManager.getValueBoolean("Agent.eyesblinking.enabled"));

        // Init the Lip Model
        lipModel = new LipModel();

        // Init the Simple AUs Peformer
        simpleAUPerformer = new SimpleAUPerformer();

        // Init the Lip Blender
        lipBlender = new LipBlender();

        // Init the Audio Key Frame Peformer
        audioKeyframePerformer = new AudioKeyFramePerformer();

        // Init the Environment Manager
        environment = new Environment(iniManager.getValueString("Environment"));

        // Init the Ogre3D Player
        playerOgre = new OgreFrame();
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
        tTS = new TTSController();
        tTS.setDoTemporize(true);
        tTS.setDoAudio(true);
        tTS.setDoPhonemes(true);
        tTS.dispose();

        // Instantiate the specific TTS (Cereproc)
        cereproc_TTS = new CereprocTTS();

        // Init the Mpeg4 Animation Table
        mPEG4Animatable = new MPEG4Animatable();     
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
        animationKeyframePerformer = new AnimationKeyframePerformer();
        animationKeyframePerformer.setUsePropagation(true);
        animationKeyframePerformer.setWeightPropagation(0.2);

        // Creates the Interruption Manager
        interruptionManager = new InterruptionManager();
        
        // Creates the Interupption Tester
        // Init the Interruptions Tester window
        interruptionTester = new InterruptionTester();
        if(icon!=null){interruptionTester.setIconImage(icon);}
        interruptionTester.setTitle("Interruption Tester");
        interruptionTester.setLocation(647, 100);
        interruptionTester.setSize(423, 108);
        interruptionTester.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        interruptionTester.setVisible(iniManager.getValueBoolean("InterruptionTester.visible"));
        
        // Creates the ARIA Information States Manager
        ariaInfoStateManager = new ARIAInformationStatesManager();
        
        // Creates the ARIA Information States Tester
        ariaInfoStateManagerForm = new ARIAInformationStatesSelectorForm();
        if(icon!=null){ariaInfoStateManagerForm.setIconImage(icon);}
        ariaInfoStateManagerForm.setTitle("ARIA Information State Changer");
        ariaInfoStateManagerForm.setLocation(400, 600);
        ariaInfoStateManagerForm.setSize(400, 150);
        ariaInfoStateManagerForm.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        ariaInfoStateManagerForm.setVisible(iniManager.getValueBoolean("ARIAInformationStateTester.visible"));
        
        // Create ARIA FML Translator
        ariaValuspaFMLTranslator = new AriaValuspaFMLTranslator();
        if(icon!=null){ariaValuspaFMLTranslator.setIconImage(icon);}
        ariaValuspaFMLTranslator.setTitle("AriaValuspaFMLTranslator");
        ariaValuspaFMLTranslator.setLocation(0, 0);
        ariaValuspaFMLTranslator.setSize(640, 480);
        ariaValuspaFMLTranslator.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        ariaValuspaFMLTranslator.setVisible(iniManager.getValueBoolean("ARIAFMLTranslator.visible"));
        
        // Character Manager
        characterManager = new CharacterIniManagerFrame();
        characterManager.setCurrentCaracter(iniManager.getValueString("Agent.appearence"));
        if(icon!=null){characterManager.setIconImage(icon);}
        characterManager.setTitle("CharacterManager");
        characterManager.setLocation(16, 844);
        characterManager.setSize(505, 532);
        characterManager.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        characterManager.setVisible(iniManager.getValueBoolean("CharacterManager.visible"));
        
        // Create the ActiveMQ FML Receiver
        amqFMLReceiver = new FMLReceiver();
        amqFMLReceiver.setHost(iniManager.getValueString("FMLReceiver.host"));
        amqFMLReceiver.setPort(iniManager.getValueString("FMLReceiver.port"));
        amqFMLReceiver.setTopic(iniManager.getValueString("FMLReceiver.topic"));
        amqFMLReceiver.setIsQueue(iniManager.getValueBoolean("FMLReceiver.isQueue"));
        amqFMLReceiverModuleFrame = new WhitboardFrame();
        amqFMLReceiverModuleFrame.setWhitboard(amqFMLReceiver);
        if(icon!=null){amqFMLReceiverModuleFrame.setIconImage(icon);}
        amqFMLReceiverModuleFrame.setTitle("FML Receiver");
        amqFMLReceiverModuleFrame.setLocation(1082, 10);
        amqFMLReceiverModuleFrame.setSize(260, 240);
        amqFMLReceiverModuleFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        amqFMLReceiverModuleFrame.setVisible(iniManager.getValueBoolean("WhiteBoard.visible"));

        // Create the ActiveMQ BML Feedback Sender
        amqBMLCallbacksSender = new BMLCallbacksSender();
        amqBMLCallbacksSender.setHost(iniManager.getValueString("BMLFeedbackSender.host"));
        amqBMLCallbacksSender.setPort(iniManager.getValueString("BMLFeedbackSender.port"));
        amqBMLCallbacksSender.setTopic(iniManager.getValueString("BMLFeedbackSender.topic"));
        amqBMLCallbacksSender.setIsQueue(iniManager.getValueBoolean("BMLFeedbackSender.isQueue"));
        amqBMLCallbacksSenderModuleFrame = new WhitboardFrame();
        amqBMLCallbacksSenderModuleFrame.setWhitboard(amqBMLCallbacksSender);
        if(icon!=null){amqBMLCallbacksSenderModuleFrame.setIconImage(icon);}
        amqBMLCallbacksSenderModuleFrame.setTitle("BML Feedback Sender");
        amqBMLCallbacksSenderModuleFrame.setLocation(1382, 10);
        amqBMLCallbacksSenderModuleFrame.setSize(260, 240);
        amqBMLCallbacksSenderModuleFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        amqBMLCallbacksSenderModuleFrame.setVisible(iniManager.getValueBoolean("WhiteBoard.visible"));

        // Create the ActiveMQ BML Sender
        amqBMLSender = new vib.auxiliary.activemq.semaine.BMLSender();
        amqBMLSender.setHost(iniManager.getValueString("BMLSender.host"));
        amqBMLSender.setPort(iniManager.getValueString("BMLSender.port"));
        amqBMLSender.setTopic(iniManager.getValueString("BMLSender.topic"));
        amqBMLSender.setIsQueue(iniManager.getValueBoolean("BMLSender.isQueue"));
        amqBMLSenderModuleFrame = new WhitboardFrame();
        amqBMLSenderModuleFrame.setWhitboard(amqBMLSender);
        if(icon!=null){amqBMLSenderModuleFrame.setIconImage(icon);}
        amqBMLSenderModuleFrame.setTitle("BML Sender");
        amqBMLSenderModuleFrame.setLocation(1382, 40);
        amqBMLSenderModuleFrame.setSize(260, 240);
        amqBMLSenderModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqBMLSenderModuleFrame.setVisible(iniManager.getValueBoolean("WhiteBoard.visible"));
        
        // Create the ActiveMQ BML Receiver
        amqBMLReceiver = new vib.auxiliary.activemq.semaine.BMLReceiver();
        amqBMLReceiver.setHost(iniManager.getValueString("BMLReceiver.host"));
        amqBMLReceiver.setPort(iniManager.getValueString("BMLReceiver.port"));
        amqBMLReceiver.setTopic(iniManager.getValueString("BMLReceiver.topic"));
        amqBMLReceiver.setIsQueue(iniManager.getValueBoolean("BMLReceiver.isQueue"));
        amqBMLReceiverModuleFrame = new WhitboardFrame();
        amqBMLReceiverModuleFrame.setWhitboard(amqBMLReceiver);
        if(icon!=null){amqBMLReceiverModuleFrame.setIconImage(icon);}
        amqBMLReceiverModuleFrame.setTitle("BML Receiver");
        amqBMLReceiverModuleFrame.setLocation(1382, 80);
        amqBMLReceiverModuleFrame.setSize(260, 240);
        amqBMLReceiverModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqBMLReceiverModuleFrame.setVisible(iniManager.getValueBoolean("WhiteBoard.visible"));
        
        // Create the ActiveMQ Information States Receiver
        amqARIAInfoStateReceiver = new vib.auxiliary.activemq.aria.ARIAInformationStateReceiver();
        amqARIAInfoStateReceiver.setHost(iniManager.getValueString("ARIAInfoStateReceiver.host"));
        amqARIAInfoStateReceiver.setPort(iniManager.getValueString("ARIAInfoStateReceiver.port"));
        amqARIAInfoStateReceiver.setTopic(iniManager.getValueString("ARIAInfoStateReceiver.topic"));
        amqARIAInfoStateReceiver.setIsQueue(iniManager.getValueBoolean("ARIAInfoStateReceiver.isQueue"));
        amqARIAInfoStateReceiverModuleFrame = new WhitboardFrame();
        amqARIAInfoStateReceiverModuleFrame.setWhitboard(amqARIAInfoStateReceiver);
        if(icon!=null){amqBMLReceiverModuleFrame.setIconImage(icon);}
        amqARIAInfoStateReceiverModuleFrame.setTitle("ARIA Information State Receiver");
        amqARIAInfoStateReceiverModuleFrame.setLocation(900, 100);
        amqARIAInfoStateReceiverModuleFrame.setSize(260, 240);
        amqARIAInfoStateReceiverModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        amqARIAInfoStateReceiverModuleFrame.setVisible(false);
        
        // Create a body animation noise generator
        bodyNoiseGenerator = new BodyAnimationNoiseGenerator();
        bodyNoiseGenerator.setUseLowerBody(false);
        bodyNoiseGenerator.setUseTorso(true);
        bodyNoiseGenerator.setUseHead(true);
        bodyNoiseGenerator.setIntensityHead(0.7);
        bodyNoiseGenerator.setIntensityTorso(0.6);
        
        // Create the video capture controller
        captureController = new Capturecontroller();
        captureController.setRealTimeCapture(false);
        captureController.setUseTexture(false); 
        if(icon!=null){captureController.setIconImage(icon);}
        captureController.setTitle("Capture Controller");
        captureController.setLocation(500, 700);
        captureController.setSize(500, 160);
        captureController.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        captureController.setVisible(iniManager.getValueBoolean("Player.captureController.enabled"));
        
        // Creae the Xuggle Video Capture module
        xuggleVideoCapture = new XuggleVideoCapture();
        CodecSelectorFrame = new CodecSelector();
        CodecSelectorFrame.setXuggleVideoCapture(xuggleVideoCapture);
        if(icon!=null){CodecSelectorFrame.setIconImage(icon);}
        CodecSelectorFrame.setTitle("Codec Selector");
        CodecSelectorFrame.setLocation(0, 700);
        CodecSelectorFrame.setSize(500, 160);
        CodecSelectorFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        CodecSelectorFrame.setVisible(iniManager.getValueBoolean("Player.captureController.enabled"));
        
        // Create the ARIA Social Attitude Planner
        ariaSocialAttitudesPlanner = new ARIASocialAttitudesPlanner();
        if(icon!=null){ariaSocialAttitudesPlanner.setIconImage(icon);}
        ariaSocialAttitudesPlanner.setTitle("Social Attitudes Planner");
        ariaSocialAttitudesPlanner.setLocation(800, 700);
        ariaSocialAttitudesPlanner.setSize(400, 100);
        ariaSocialAttitudesPlanner.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        ariaSocialAttitudesPlanner.setVisible(iniManager.getValueBoolean("SocialAttitudesPlanner.enabled"));
        ariaSocialAttitudesPlanner.setComputeSocialAttitudeExpression(iniManager.getValueBoolean("SocialAttitudesPlanner.enabled"));

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
        captureController.setCapturable(playerOgre);
        captureController.setCaptureOutput(xuggleVideoCapture);
        behaviorPlanner.addSignalPerformer(ariaSocialAttitudesPlanner);
        ariaSocialAttitudesPlanner.addSignalPerformer(behaviorRealizer);
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
