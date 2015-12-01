package eu.aria.core.demo;

import eu.aria.util.data.MovingAverage;
import eu.aria.util.types.EMaxData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by adg on 16/11/2015.
 *
 */
public class EMaxFilter {

    public static final int SAMPLE_SIZE = 20;

    private MovingAverage anger = new MovingAverage(SAMPLE_SIZE);
    private MovingAverage disgust = new MovingAverage(SAMPLE_SIZE);
    private MovingAverage fear = new MovingAverage(SAMPLE_SIZE);
    private MovingAverage happiness = new MovingAverage(SAMPLE_SIZE);
    private MovingAverage sadness = new MovingAverage(SAMPLE_SIZE);
    private MovingAverage surprised = new MovingAverage(SAMPLE_SIZE);

    private EMaxListener listener;
    private EMaxThresholdDialog eth;

    private Comparator<EMaxThresholdDialog.DoubleStringEntry> reverseComparator = Collections.reverseOrder(new EMaxThresholdDialog.TupleComparator());

    public EMaxFilter(EMaxThresholdDialog eth) {
        this.eth = eth;
    }

    public void setListener(EMaxListener listener) {
        this.listener = listener;
    }

    public void onEMaxData(EMaxData data) {
        List<EMaxData.EMaxFace> eMaxFaces = data.getFaces();
        if (!eMaxFaces.isEmpty()) {
            EMaxData.EMaxFace face = eMaxFaces.get(0);

            anger.addValue(face.getAnger());
            disgust.addValue(face.getDisgust());
            fear.addValue(face.getFear());
            happiness.addValue(face.getHappiness());
            sadness.addValue(face.getSadness());
            surprised.addValue(face.getSurprised());

            if (listener != null) {
                double angerV = anger.getAverage() + eth.getAnger(), disgustV = disgust.getAverage() + eth.getDisgust(),
                        fearV = fear.getAverage() + eth.getFear(), happinesV = happiness.getAverage() + eth.getHappiness(),
                        sadnessV = sadness.getAverage() + eth.getSadness(), surprisedV = surprised.getAverage() + eth.getSurprised();

                String ANGE = "anger", DISG = "disgust", FEAR = "fear",
                        HAPP = "happiness", SADN = "sadness", SURP = "surprised";
                double[] emValues = {angerV, disgustV, fearV, happinesV, sadnessV, surprisedV};
                String[] EMOTIONS = {ANGE, DISG, FEAR, HAPP, SADN, SURP};

                ArrayList<EMaxThresholdDialog.DoubleStringEntry> list = new ArrayList<>();
                for (int i = 0; i < emValues.length; i++) {
                    list.add(new EMaxThresholdDialog.DoubleStringEntry(emValues[i], EMOTIONS[i]));
                }
                list.sort(reverseComparator);
                EMaxThresholdDialog.DoubleStringEntry best = list.get(0);

                String text = "Anger: " + angerV + "\n" +
                        "Disgust: " + disgustV + "\n" +
                        "Fear: " + fearV + "\n" +
                        "Happiness: " + happinesV + "\n" +
                        "Sadness: " + sadnessV + "\n" +
                        "Surprised: " + surprisedV + "\n\n\n" +
                        best.getValue() + ": " + best.getKey();

                listener.onText(text);
            }
        }
    }

    public interface EMaxListener {
        void onText(String text);
    }
}
