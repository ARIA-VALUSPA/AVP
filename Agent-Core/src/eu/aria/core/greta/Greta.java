/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aria.core.greta;

import vib.auxiliary.player.ogre.OgreFrame;
import vib.auxiliary.tts.cereproc.CereprocTTS;
import vib.core.animation.lipmodel.LipModel;
import vib.core.animation.mpeg4.MPEG4Animatable;
import vib.core.animation.mpeg4.fap.filters.LipBlender;
import vib.core.animation.performer.AnimationKeyframePerformer;
import vib.core.behaviorrealizer.Realizer;
import vib.core.intentions.FMLFileReader;
import vib.core.intentions.FMLTranslator;
import vib.core.intentions.Intention;
import vib.core.intentions.IntentionPerformer;
import vib.core.keyframes.AudioKeyFramePerformer;
import vib.core.keyframes.face.FaceKeyframePerformer;
import vib.core.keyframes.face.SimpleAUPerformer;
import vib.core.socialintentionfilter.SocialIntentionFilterPlanner;
import vib.core.util.Mode;
import vib.core.util.environment.Environment;
import vib.core.util.id.ID;
import vib.core.util.id.IDProvider;
import vib.core.util.xml.XML;
import vib.core.util.xml.XMLParser;
import vib.core.util.xml.XMLTree;
import vib.core.utilx.gui.*;

import javax.swing.*;
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adg
 */
public class Greta {

    private SocialIntentionFilterPlanner socialIntentionFilterPlanner = new SocialIntentionFilterPlanner();
    private XMLParser fmlParser = XML.createParser();

    public Greta() {
    }

    public void init() {
        try {
            updatePluginsCfg();
        } catch (IOException e) {
            System.out.println("WARNING: Failed to modify Plugins_OpenGL.cfg");
        }

        // Load the icon for the frames (if found)
        java.awt.Image icon = null;
        java.net.URL url = Greta.class.getClassLoader().getResource("icon.png");
        if(url!=null) {icon = java.awt.Toolkit.getDefaultToolkit().getImage(url);}

        // Init the BML Realizer
        Realizer behaviorRealizer = new Realizer();

        // Init the Face Key Frame Peformer
        FaceKeyframePerformer faceKeyframePerformer = new FaceKeyframePerformer();
        faceKeyframePerformer.setBlinking(true);

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
        playerOgre.setCameraPositionX(10.109793663024902);
        playerOgre.setCameraPositionY(1.8801536560058594);
        playerOgre.setCameraPositionZ(2.1462271213531494);
        playerOgre.setCameraPitch(-0.12615139782428741);
        playerOgre.setCameraYaw(2.951481342315674);
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
        mPEG4Animatable.setCoordinateX(8.9);
        mPEG4Animatable.setCoordinateY(0.0);
        mPEG4Animatable.setCoordinateZ(5.6);
        mPEG4Animatable.setOrientationX(0.0);
        mPEG4Animatable.setOrientationY(0.8);
        mPEG4Animatable.setOrientationZ(0.0);
        mPEG4Animatable.setOrientationW(0.17);
        mPEG4Animatable.setScaleX(1.0);
        mPEG4Animatable.setScaleY(1.0);
        mPEG4Animatable.setScaleZ(1.0);

        // Init the Animation Key Frame Perfomer
        AnimationKeyframePerformer animationKeyframePerformer = new AnimationKeyframePerformer();
        animationKeyframePerformer.setUsePropagation(false);
        animationKeyframePerformer.setWeightPropagation(0.1);

        // Connect the modules
        simpleAUPerformer.addFAPFramePerformer(lipBlender);
        animationKeyframePerformer.addBAPFramesPerformer(mPEG4Animatable);
        socialIntentionFilterPlanner.addSignalPerformer(behaviorRealizer);
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
    }

    public void perform(String fml, String id) {
        perform(fmlParser.parseBuffer(fml), IDProvider.createID(id));
    }

    public void perform(XMLTree fml, ID id) {
        Mode mode = FMLTranslator.DEFAULT_FML_MODE;
        if (fml.hasAttribute("composition")) {
            mode = Mode.interpret(fml.getAttribute("composition"), mode);
        }
        perform(FMLTranslator.FMLToIntentions(fml), id, mode);
    }

    public void perform(List<Intention> intentions, ID id, Mode mode) {
        socialIntentionFilterPlanner.performIntentions(intentions, id, mode);
    }

    public IntentionPerformer getIntentionPerformer() {
        return socialIntentionFilterPlanner;
    }

    private boolean updatePluginsCfg() throws IOException {
        File file = new File("Player\\Lib\\External\\Win64\\Plugins_OpenGL.cfg");
        File fileBk = new File(file.getAbsolutePath() + ".bk");

        if (fileBk.exists()) {
            System.out.println("Plugins_OpenGL.cfg already updated.");
            return false;
        }

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        Files.copy(file.toPath(), fileBk.toPath());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (String l : lines) {
                bw.write(l);
            }
        }

        System.out.println("Plugins_OpenGL.cfg updated successfully.");

        return true;
    }
}
