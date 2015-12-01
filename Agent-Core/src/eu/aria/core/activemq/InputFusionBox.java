package eu.aria.core.activemq;

import eu.aria.core.Config;
import eu.aria.util.types.AGenderData;
import eu.aria.util.types.ASRData;
import eu.aria.util.types.EMaxData;
import vib.auxiliary.activemq.Broker;
import vib.auxiliary.activemq.gui.WhitboardFrame;
import vib.auxiliary.ssi.SSIFilterForm;
import vib.auxiliary.ssi.SSIFrame;
import vib.auxiliary.ssi.SSIFramePerfomer;
import vib.auxiliary.ssi.SSIXMLToFrameTranslator;
import vib.core.util.IniManager;
import vib.core.util.id.ID;

import java.util.HashSet;
import java.util.List;

/**
 * Created by adg on 02/11/2015.
 */
public class InputFusionBox {

    private IniManager iniManager;
    private Config config;

    private HashSet<ASRListener> asrListeners = new HashSet<>();
    private HashSet<EMaxListener> eMaxListeners = new HashSet<>();
    private HashSet<AGenderListener> aGenderListeners = new HashSet<>();

    public void init(Config config) {
        init(config, true, true, true);
    }

    public void init(Config config, boolean agender, boolean emax, boolean asr) {
        if (config == null) {
            this.config = Config.DEFAULT;
        } else {
            this.config = config;
        }

        iniManager = new IniManager("./Agent-Core.ini");

        if (agender) {
            initAGender();
        }
        if (emax) {
            initEMax();
        }
        if (asr) {
            initASR();
        }
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

    private void initAGender() {
        // Load the icon for the frames (if found)
        java.awt.Image icon = null;
        java.net.URL url = InputFusionBox.class.getClassLoader().getResource("icon.png");
        if (url != null) {
            icon = java.awt.Toolkit.getDefaultToolkit().getImage(url);
        }

        // Read the .ini file and initialize the system

        // Init the SSI XML-to-SSIFrame Translator
        SSIXMLToFrameTranslator ssiTranslatorAGeneder = new SSIXMLToFrameTranslator();
        ssiTranslatorAGeneder.setHost(iniManager.getValueString("AGENDER.host"));
        ssiTranslatorAGeneder.setPort(iniManager.getValueString("AGENDER.port"));
        ssiTranslatorAGeneder.setTopic(iniManager.getValueString("AGENDER.topic"));
        ssiTranslatorAGeneder.setIsQueue(iniManager.getValueBoolean("AGENDER.isQueue"));
        WhitboardFrame ssiTranslatorModuleFrame = new WhitboardFrame();
        ssiTranslatorModuleFrame.setWhitboard(ssiTranslatorAGeneder);
        if (icon != null) {
            ssiTranslatorModuleFrame.setIconImage(icon);
        }
        ssiTranslatorModuleFrame.setTitle("SSITranslator - AGENDER");
        ssiTranslatorModuleFrame.setLocation(1082, 9);
        ssiTranslatorModuleFrame.setSize(240, 220);
        ssiTranslatorModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        ssiTranslatorModuleFrame.setVisible(config.showSSIWindows());

        // Init the SSI Filter Window
        SSIFilterForm ssiFilter = new SSIFilterForm();
        if (icon != null) {
            ssiFilter.setIconImage(icon);
        }
        ssiFilter.setTitle("SSIFilter - AGENDER");
        ssiFilter.setLocation(1079, 241);
        ssiFilter.setSize(239, 104);
        ssiFilter.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        ssiFilter.setVisible(config.showSSIWindows());

        // Create the ActiveMQ broker
        Broker activeMQBroker = new Broker();
        activeMQBroker.setPort(iniManager.getValueString("ActiveMQBroker.port"));

        // Connect the modules
        ssiFilter.setSSITranslator(ssiTranslatorAGeneder);

        ssiTranslatorAGeneder.addSSIFramePerformer(new SSIFramePerfomer() {
            @Override
            public void performSSIFrames(List<SSIFrame> list, ID id) {
                for (SSIFrame frame : list) {
                    performSSIFrame(frame, id);
                }
            }

            @Override
            public void performSSIFrame(SSIFrame ssiFrame, ID id) {
                for (AGenderListener listener : aGenderListeners) {
                    try {
                        listener.onAGenderData(AGenderData.FromSSIFrame(ssiFrame));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initEMax() {
        // Load the icon for the frames (if found)
        java.awt.Image icon = null;
        java.net.URL url = InputFusionBox.class.getClassLoader().getResource("icon.png");
        if (url != null) {
            icon = java.awt.Toolkit.getDefaultToolkit().getImage(url);
        }

        // Init the SSI XML-to-SSIFrame Translator
        SSIXMLToFrameTranslator ssiTranslatorEMax = new SSIXMLToFrameTranslator();
        ssiTranslatorEMax.setHost(iniManager.getValueString("EMax.host"));
        ssiTranslatorEMax.setPort(iniManager.getValueString("EMax.port"));
        ssiTranslatorEMax.setTopic(iniManager.getValueString("EMax.topic"));
        ssiTranslatorEMax.setIsQueue(iniManager.getValueBoolean("EMax.isQueue"));
        WhitboardFrame ssiTranslatorModuleFrame = new WhitboardFrame();
        ssiTranslatorModuleFrame.setWhitboard(ssiTranslatorEMax);
        if (icon != null) {
            ssiTranslatorModuleFrame.setIconImage(icon);
        }
        ssiTranslatorModuleFrame.setTitle("SSITranslator - EMax");
        ssiTranslatorModuleFrame.setLocation(1082, 9);
        ssiTranslatorModuleFrame.setSize(240, 220);
        ssiTranslatorModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        ssiTranslatorModuleFrame.setVisible(config.showSSIWindows());

        // Init the SSI Filter Window
        SSIFilterForm ssiFilter = new SSIFilterForm();
        if (icon != null) {
            ssiFilter.setIconImage(icon);
        }
        ssiFilter.setTitle("SSIFilter - EMax");
        ssiFilter.setLocation(1079, 241);
        ssiFilter.setSize(239, 104);
        ssiFilter.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        ssiFilter.setVisible(config.showSSIWindows());

        // Create the ActiveMQ broker
        Broker activeMQBroker = new Broker();
        activeMQBroker.setPort(iniManager.getValueString("ActiveMQBroker.port"));

        // Connect the modules
        ssiFilter.setSSITranslator(ssiTranslatorEMax);

        ssiTranslatorEMax.addSSIFramePerformer(new SSIFramePerfomer() {
            @Override
            public void performSSIFrames(List<SSIFrame> list, ID id) {
                for (SSIFrame frame : list) {
                    performSSIFrame(frame, id);
                }
            }

            @Override
            public void performSSIFrame(SSIFrame ssiFrame, ID id) {
                for (EMaxListener listener : eMaxListeners) {
                    try {
                        listener.onEMaxData(EMaxData.FromSSIFrame(ssiFrame));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initASR() {
        // Load the icon for the frames (if found)
        java.awt.Image icon = null;
        java.net.URL url = InputFusionBox.class.getClassLoader().getResource("icon.png");
        if (url != null) {
            icon = java.awt.Toolkit.getDefaultToolkit().getImage(url);
        }

        // Init the SSI XML-to-SSIFrame Translator
        SSIXMLToFrameTranslator ssiTranslatorEMax = new SSIXMLToFrameTranslator();
        ssiTranslatorEMax.setHost(iniManager.getValueString("ASR.host"));
        ssiTranslatorEMax.setPort(iniManager.getValueString("ASR.port"));
        ssiTranslatorEMax.setTopic(iniManager.getValueString("ASR.topic"));
        ssiTranslatorEMax.setIsQueue(iniManager.getValueBoolean("ASR.isQueue"));
        WhitboardFrame ssiTranslatorModuleFrame = new WhitboardFrame();
        ssiTranslatorModuleFrame.setWhitboard(ssiTranslatorEMax);
        if (icon != null) {
            ssiTranslatorModuleFrame.setIconImage(icon);
        }
        ssiTranslatorModuleFrame.setTitle("SSITranslator - ASR");
        ssiTranslatorModuleFrame.setLocation(1082, 9);
        ssiTranslatorModuleFrame.setSize(240, 220);
        ssiTranslatorModuleFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        ssiTranslatorModuleFrame.setVisible(config.showSSIWindows());

        // Init the SSI Filter Window
        SSIFilterForm ssiFilter = new SSIFilterForm();
        if (icon != null) {
            ssiFilter.setIconImage(icon);
        }
        ssiFilter.setTitle("SSIFilter - ASR");
        ssiFilter.setLocation(1079, 241);
        ssiFilter.setSize(239, 104);
        ssiFilter.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        ssiFilter.setVisible(config.showSSIWindows());

        // Create the ActiveMQ broker
        Broker activeMQBroker = new Broker();
        activeMQBroker.setPort(iniManager.getValueString("ActiveMQBroker.port"));

        // Connect the modules
        ssiFilter.setSSITranslator(ssiTranslatorEMax);

        ssiTranslatorEMax.addSSIFramePerformer(new SSIFramePerfomer() {
            @Override
            public void performSSIFrames(List<SSIFrame> list, ID id) {
                for (SSIFrame frame : list) {
                    performSSIFrame(frame, id);
                }
            }

            @Override
            public void performSSIFrame(SSIFrame ssiFrame, ID id) {
                for (ASRListener listener : asrListeners) {
                    try {
                        listener.onASRData(ASRData.FromSSIFrame(ssiFrame));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
}
