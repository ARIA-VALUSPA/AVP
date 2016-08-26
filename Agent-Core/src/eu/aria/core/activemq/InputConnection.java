package eu.aria.core.activemq;

import eu.aria.core.Config;
import eu.aria.util.XMLReceiver;
import eu.aria.util.activemq.util.UrlBuilder;
import eu.aria.util.types.AGenderData;
import eu.aria.util.types.ASRData;
import eu.aria.util.types.AudioEmotionData;
import eu.aria.util.types.EMaxData;
import vib.core.util.IniManager;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by adg on 05/07/2016.
 *
 */
public class InputConnection {
    private IniManager iniManager;
    private Config config;

    private HashSet<ASRListener> asrListeners = new HashSet<>();
    private HashSet<EMaxListener> eMaxListeners = new HashSet<>();
    private HashSet<AGenderListener> aGenderListeners = new HashSet<>();
    private HashSet<AudioEmotionListener> audioEmotionListeners = new HashSet<>();

    private Executor executor = Executors.newFixedThreadPool(4);

    public void init(Config config, IniManager iniManager) {
        if (config == null) {
            this.config = Config.DEFAULT;
        } else {
            this.config = config;
        }

        this.iniManager = iniManager;

        File schema = new File(iniManager.getValueString("SSI.schema"));

        if (!schema.exists()) {
            System.err.println("Schema file does not exit.");
            return;
        }

        initInternal(schema);
    }

    public void addEMaxListener(EMaxListener listener) {
        eMaxListeners.add(listener);
    }

    public void removeEMaxListener(EMaxListener listener) {
        eMaxListeners.remove(listener);
    }

    public void addAGenderListener(AGenderListener listener) {
        aGenderListeners.add(listener);
    }

    public void removeAGenderListener(AGenderListener listener) {
        aGenderListeners.remove(listener);
    }

    public void addASRListener(ASRListener listener) {
        asrListeners.add(listener);
    }

    public void removeASRListener(ASRListener listener) {
        asrListeners.remove(listener);
    }

    public void addAudioEmotionListener(AudioEmotionListener listener) {
        audioEmotionListeners.add(listener);
    }

    public void removeAudioEmotionListener(AudioEmotionListener listener) {
        audioEmotionListeners.remove(listener);
    }

    private void initInternal(File schema) {
        XMLReceiver receiver = new XMLReceiver();
        receiver.setURL(UrlBuilder.getUrlTcp(iniManager.getValueString("AMQ.host"), iniManager.getValueString("AMQ.port")));
        receiver.setDestinationName(iniManager.getValueString("AMQ.ssi.topic"));
        receiver.setUseTopic(iniManager.getValueBoolean("AMQ.ssi.isTopic"));

        boolean ready = receiver.readSchema(schema);

        receiver.registerListener(new XMLReceiver.ObjectListener<EMaxData>() {

            @Override
            public Class<EMaxData> getType() {
                return EMaxData.class;
            }

            @Override
            public void onNewData(EMaxData data) {
                sendEMaxData(data);
            }
        });

        receiver.registerListener(new XMLReceiver.ObjectListener<AGenderData>() {

            @Override
            public Class<AGenderData> getType() {
                return AGenderData.class;
            }

            @Override
            public void onNewData(AGenderData data) {
                sendAGenderData(data);
            }
        });

        receiver.registerListener(new XMLReceiver.ObjectListener<ASRData>() {

            @Override
            public Class<ASRData> getType() {
                return ASRData.class;
            }

            @Override
            public void onNewData(ASRData data) {
                sendASRData(data);
            }
        });

        receiver.registerListener(new XMLReceiver.ObjectListener<AudioEmotionData>() {
            @Override
            public Class<AudioEmotionData> getType() {
                return AudioEmotionData.class;
            }

            @Override
            public void onNewData(AudioEmotionData data) {
                sendAudioEmotionData(data);
            }
        });

        if (ready) {
            receiver.start(config.showSSIWindows());
        } else {
            System.err.println("Could not start the activemq receiver!");
        }
    }

    private void sendAudioEmotionData(AudioEmotionData data) {
        executor.execute(() -> {
            for (AudioEmotionListener listener : audioEmotionListeners) {
                try {
                    listener.onAudioEmotionData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendAGenderData(AGenderData data) {
        executor.execute(() -> {
            if (data == null) {
                return;
            }

            for (AGenderListener listener : aGenderListeners) {
                try {
                    listener.onAGenderData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendASRData(ASRData data) {
        executor.execute(() -> {
            if (data == null) {
                return;
            }

            for (ASRListener listener : asrListeners) {
                try {
                    listener.onASRData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendEMaxData(EMaxData data) {
        executor.execute(() -> {
            for (EMaxListener listener : eMaxListeners) {
                try {
                    listener.onEMaxData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface EMaxListener {
        void onEMaxData(EMaxData data);
    }

    public interface AGenderListener {
        void onAGenderData(AGenderData data);
    }

    public interface ASRListener {
        void onASRData(ASRData data);
    }

    public interface AudioEmotionListener {
        void onAudioEmotionData(AudioEmotionData data);
    }
}
