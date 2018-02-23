package eu.aria.dm.gui;

import eu.aria.dm.util.Say;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

/**
 * Created by WaterschootJB on 3-7-2017.
 */
public class GUIController extends Observable {

    private static HashMap<String, GUIController> instances = new HashMap<>();
    private final String is;

    public synchronized static GUIController getInstance(String informationState) {
        GUIController inst = instances.get(informationState);
        if ( inst == null) {
            inst = new GUIController(informationState);
            instances.put(informationState, inst);
            UserAndAgentGUI gui = UserAndAgentGUI.getInstance(inst);
            inst.addObserver(gui);
        }
        return inst;
    }

    private GUIController(String is){
        this.is = is;
    }

    private final LinkedList<Say> agentSays = new LinkedList<>();
    private final LinkedList<Say> userSays = new LinkedList<>();
    private final LinkedList<Say> allSays = new LinkedList<>();

    private Say bufferedUserSay = null;
    public synchronized void addAgentSay(Say say /*, boolean updateIS*/) {
        agentSays.addLast(say);
        allSays.addLast(say);
        this.notifyChanged(new DataChanged(DataChangedType.AgentSay, say));
    }

    public synchronized void addUserSay(Say say/*, boolean updateIS*/) {
        userSays.addLast(say);
        allSays.addLast(say);
        bufferedUserSay = say;
        this.notifyChanged(new DataChanged(DataChangedType.UserSay, say));
    }

    public synchronized Say[] getAgentSays() {
        Say[] result = new Say[agentSays.size()];
        return agentSays.toArray(result);
    }

    public synchronized Say getLastUserSay(){
        Say res = bufferedUserSay;
        bufferedUserSay = null;
        return res;
    }

    public synchronized Say[] getUserSays() {
        Say[] result = new Say[userSays.size()];
        return userSays.toArray(result);
    }

    public synchronized Say[] getAllSays() {
        Say[] result = new Say[allSays.size()];
        return allSays.toArray(result);
    }
    double valence = 0.5;
    public synchronized void setValence(double value) {
        valence = value;
    }

    public synchronized double getValence(){
        return valence;
    }
    double interest = 0.5;
    public synchronized double getInterest(){
        return interest;
    }

    public synchronized void setInterest(double value){
        interest = value;
    }

    public synchronized void setArousal(double value) {
        arousal = 0.5;
    }
    double arousal = 0.5;
    public synchronized double getArousal(){
        return arousal;
    }

    boolean isTalking = false;
    public synchronized void setIsTalking(boolean isTalking){
        this.isTalking = isTalking;
    }

    public synchronized boolean getIsTalking(){
        return isTalking;
    }


    public synchronized void updatePresent(boolean present){
        isPresent = present;
        notifyChanged(new DataChanged(DataChangedType.PresenceChange, present));
    }
    public void setPresent(boolean present){
        isPresent = present;
    }

    boolean isPresent = false;
    public boolean getPresent(){
        return isPresent;
    }

    boolean overrideEmo = false;
    public synchronized  void setOverrideEmo(boolean override){
        overrideEmo = override;
    }
    boolean blockASR;
    public synchronized void setBlockASR(boolean override){
        blockASR = override;
    }
    boolean overridePresent = false;
    public synchronized void setOverridePresent(boolean override){
        overridePresent = override;
    }
    public synchronized boolean getOverridePresent(){
        return overridePresent;
    }
    public synchronized boolean getOverrideEmo(){
        return overrideEmo;
    }
    public synchronized boolean getBlockASR(){
        return blockASR;
    }
    public class DataChanged {

        private final DataChangedType type;
        private final Object argument;

        public DataChangedType getType() {
            return type;
        }

        public Object getArgument() {
            return argument;
        }

        public DataChanged(DataChangedType type, Object argument) {
            this.type = type;
            this.argument = argument;
        }
    }

    public enum DataChangedType {
        AgentSay,
        UserSay,
        EmotionChange,
        PresenceChange
    }

    public class EmotionKV {

        private String emotionName;
        private Double emotionValue;
        EmotionKV(String name, Double value){
            emotionName = name;
            emotionValue = value;
        }
        public String getEmotionName() {
            return emotionName;
        }

        public void setEmotionName(String emotionName) {
            this.emotionName = emotionName;
        }

        public Double getEmotionValue() {
            return emotionValue;
        }

        public void setEmotionValue(Double emotionValue) {
            this.emotionValue = emotionValue;
        }
    }
    private void notifyChanged(DataChanged object){
        setChanged();
        notifyObservers(object);
    }


}
