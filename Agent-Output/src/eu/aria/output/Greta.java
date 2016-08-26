package eu.aria.output;

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
import vib.core.keyframes.AudioKeyFramePerformer;
import vib.core.keyframes.face.FaceKeyframePerformer;
import vib.core.keyframes.face.SimpleAUPerformer;
import vib.core.util.IniManager;
import vib.core.util.environment.Environment;
import vib.core.utilx.gui.TTSController;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 * Created by adg on 17/08/2016.
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
        Environment environment = new Environment();

        // Init the Ogre3D Player
        OgreFrame playerOgre = new OgreFrame();
        playerOgre.setCameraPositionX(9.6);
        playerOgre.setCameraPositionY(1.8);
        playerOgre.setCameraPositionZ(1);
        playerOgre.setCameraPitch(-0.15);
        playerOgre.setCameraYaw(-3.0);
        playerOgre.setCameraRoll(0.0);
        if(icon!=null){playerOgre.setIconImage(icon);}
        playerOgre.setTitle("PlayerOgre");
        playerOgre.setLocation(9, 6);
        playerOgre.setSize(631, 542);
        playerOgre.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        mPEG4Animatable.setCoordinateX(initManager.getValueDouble("MPEG4Animatable.posX"));
        mPEG4Animatable.setCoordinateY(initManager.getValueDouble("MPEG4Animatable.posY"));
        mPEG4Animatable.setCoordinateZ(initManager.getValueDouble("MPEG4Animatable.posZ"));
        mPEG4Animatable.setOrientationX(initManager.getValueDouble("MPEG4Animatable.rotX"));
        mPEG4Animatable.setOrientationY(initManager.getValueDouble("MPEG4Animatable.rotY"));
        mPEG4Animatable.setOrientationZ(initManager.getValueDouble("MPEG4Animatable.rotZ"));
        mPEG4Animatable.setOrientationW(initManager.getValueDouble("MPEG4Animatable.rotW"));
        mPEG4Animatable.setScaleX(initManager.getValueDouble("MPEG4Animatable.scaleX"));
        mPEG4Animatable.setScaleY(initManager.getValueDouble("MPEG4Animatable.scaleY"));
        mPEG4Animatable.setScaleZ(initManager.getValueDouble("MPEG4Animatable.scaleZ"));

        // Init the Animation Key Frame Perfomer
        AnimationKeyframePerformer animationKeyframePerformer = new AnimationKeyframePerformer();
        animationKeyframePerformer.setUsePropagation(true);
        animationKeyframePerformer.setWeightPropagation(0.2);

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
        amqFMLReceiverModuleFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        amqFMLReceiverModuleFrame.setVisible(true);

        // Create the ActiveMQ BML Feedback Sender
        BMLCallbacksSender amqBMLCallbacksSender = new BMLCallbacksSender();
        amqBMLCallbacksSender.setHost(initManager.getValueString("BMLFeedbackSender.host"));
        amqBMLCallbacksSender.setPort(initManager.getValueString("BMLFeedbackSender.port"));
        amqBMLCallbacksSender.setTopic(initManager.getValueString("BMLFeedbackSender.topic"));
        amqBMLCallbacksSender.setIsQueue(initManager.getValueBoolean("BMLFeedbackSender.isQueue"));
        WhitboardFrame amqBMLCallbacksSenderModuleFrame = new WhitboardFrame();
        amqBMLCallbacksSenderModuleFrame.setWhitboard(amqBMLCallbacksSender);
        if(icon!=null){amqBMLCallbacksSenderModuleFrame.setIconImage(icon);}
        amqBMLCallbacksSenderModuleFrame.setTitle("BML Feedback Sender");
        amqBMLCallbacksSenderModuleFrame.setLocation(1382, 10);
        amqBMLCallbacksSenderModuleFrame.setSize(260, 240);
        amqBMLCallbacksSenderModuleFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        amqBMLCallbacksSenderModuleFrame.setVisible(true);

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
        //ssiFilter.setSSITranslator(ssiTranslator);
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
        bodyNoiseGenerator.addBAPFramesPerformer(mPEG4Animatable);
        amqFMLReceiver.addIntentionPerformer(behaviorPlanner);
        behaviorRealizer.addCallbackPerformer(amqBMLCallbacksSender);
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
