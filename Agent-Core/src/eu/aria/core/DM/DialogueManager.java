package eu.aria.core.DM;

import eu.aria.alice.interactive.AliceInteractive;
import eu.aria.core.activemq.InputFusionBox;
import eu.aria.util.types.AGenderData;
import eu.aria.util.types.ASRData;
import eu.aria.util.types.EMaxData;

import java.util.HashSet;

/**
 * Created by adg on 02/10/2015.
 */
public class DialogueManager {

    private AliceInteractive placeHolder = new AliceInteractive();
    private HashSet<FmlListener> fmlListeners = new HashSet<>();
    private HashSet<PlainListener> plainListeners = new HashSet<>();

    private String lastASRSpeech = null;

    public DialogueManager() {
        placeHolder.addFmlListener(this::onAgentFML);
        placeHolder.addListener(this::onAgentText);
    }

    public void init() {
        placeHolder.init();
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

    private void onAgentFML(String fml) {
        fmlListeners.stream().forEach(l -> l.onAgentFML(fml));
    }

    private void onAgentText(String text) {
        plainListeners.stream().forEach(l -> l.onAgentText(text));
    }

    private void onEMaxData(EMaxData data) {
        placeHolder.onEMaxData(data);
    }

    private void onAGenderData(AGenderData data) {

    }

    private void onASRData(ASRData data) {
        if (data != null && data.isActive() && (lastASRSpeech == null || !lastASRSpeech.equals(data.getSpeech()))) {
            userSays(data.getSpeech());
            lastASRSpeech = data.getSpeech();
        }
    }

    public void userSays(String text) {
        if (!placeHolder.process(text)) {
            placeHolder.reset();
        }
    }

    public InputFusionBox.EMaxListener getEMaxListener() {
        return this::onEMaxData;
    }

    public InputFusionBox.AGenderListener getAGenderListener() {
        return this::onAGenderData;
    }

    public InputFusionBox.ASRListener getASRListener() {
        return this::onASRData;
    }

    public interface FmlListener {
        void onAgentFML(String fml);
    }

    public interface PlainListener {
        void onAgentText(String text);
    }
}
