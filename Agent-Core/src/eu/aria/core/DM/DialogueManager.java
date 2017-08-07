package eu.aria.core.DM;

import eu.aria.core.activemq.InputConnection;
import eu.aria.dialogue.dm.DMPool;
import eu.aria.util.types.AGenderData;
import eu.aria.util.types.ASRData;
import eu.aria.util.types.AudioEmotionData;
import eu.aria.util.types.EMaxData;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by adg on 02/10/2015.
 *
 */
public class DialogueManager {

    private DMPool manager = DMPool.getInstance();
    private HashSet<FmlListener> fmlListeners = new HashSet<>();
    private HashSet<PlainListener> plainListeners = new HashSet<>();

    // this ensures that the getters always return the same listener (using lambda creates a new hidden object every time)
    private DMPool.ResponseListener responseListener = this::onAgentText;
    private DMPool.FMLResponseListener fmlResponseListener = this::onAgentFML;
    private InputConnection.EMaxListener eMaxListener;
    private InputConnection.AGenderListener aGenderListener;
    private InputConnection.ASRListener asrListener;
    private InputConnection.AudioEmotionListener audioEmotionListener;

    private boolean useAsrActive = false;

    private volatile boolean ready = false;

    public DialogueManager() {
        manager.addListener(responseListener);
        manager.addFmlListener(fmlResponseListener);

        eMaxListener = this::onEMaxData;
        aGenderListener = this::onAGenderData;
        asrListener = this::onASRData;
        audioEmotionListener = this::onAudioEmotionData;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                onAgentReady();
            }
        }, 7000L);
    }

    public void init() {
        manager.init(1);
    }

    public void addFmlListener(FmlListener fmlListener) {
        fmlListeners.add(fmlListener);
    }

    public void removeFmlListener(FmlListener fmlListener) {
        fmlListeners.remove(fmlListener);
    }

    public void addPlainListener(PlainListener listener) {
        plainListeners.add(listener);
    }

    public void removePlainListener(PlainListener listener) {
        plainListeners.remove(listener);
    }

    public void setUseAsrActive(boolean useAsrActive) {
        this.useAsrActive = useAsrActive;
    }

    private void onAgentFML(String fml) {
        fmlListeners.stream().forEach(l -> l.onAgentFML(fml));
    }

    private void onAgentText(String text) {
        plainListeners.stream().forEach(l -> l.onAgentText(text));
    }

    private void onEMaxData(EMaxData data) {
        manager.onEMaxData(data);
    }

    private void onAGenderData(AGenderData data) {

    }

    private void onAudioEmotionData(AudioEmotionData data) {
        manager.onAudioEmotionData(data);
    }

    private void onASRData(ASRData data) {
        if (!ready) {
            return;
        }
        if (data != null && (!useAsrActive || data.isActive()) && data.getSpeech() != null) {
            // long t1 = System.currentTimeMillis();
            // System.out.println("Received speech(" + data.getSpeech() + ") which was generated " + (t1 - data.getStartTime() * 1000) + " ms ago.");

            manager.onASRText(data.getSpeech());
        }
    }

    public void onAgentReady() {
        ready = true;
        System.out.println("Dialogue manager was marked as ready");
    }

    public InputConnection.EMaxListener getEMaxListener() {
        return eMaxListener;
    }

    public InputConnection.AGenderListener getAGenderListener() {
        return aGenderListener;
    }

    public InputConnection.ASRListener getASRListener() {
        return asrListener;
    }

    public InputConnection.AudioEmotionListener getAudioEmotionListener() {
        return audioEmotionListener;
    }

    public interface FmlListener {
        void onAgentFML(String fml);
    }

    public interface PlainListener {
        void onAgentText(String text);
    }
}
