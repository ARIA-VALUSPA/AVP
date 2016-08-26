package eu.aria.core.activemq;

import eu.aria.core.Config;
import eu.aria.core.agent.AgentFeedback;
import eu.aria.util.activemq.ActiveMQStatusGUI;
import eu.aria.util.activemq.IMessageReceiver;
import eu.aria.util.activemq.SimpleProducerWrapper;
import eu.aria.util.activemq.SimpleReceiverWrapper;
import eu.aria.util.activemq.util.UrlBuilder;
import vib.core.util.IniManager;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Created by adg on 21/08/2016.
 *
 */
public class OutputConnection {

    private IniManager iniManager;
    private Config config;

    private SimpleProducerWrapper fmlSender;
    private SimpleReceiverWrapper agentFeedback;

    private ActiveMQStatusGUI fmlSenderGUI;
    private ActiveMQStatusGUI agentFeedbackGUI;

    private IMessageReceiver receiver = this::onFeedback;

    private int fmlId = 0;

    public void init(Config config, IniManager iniManager) {
        if (config == null) {
            this.config = Config.DEFAULT;
        } else {
            this.config = config;
        }

        this.iniManager = iniManager;

        initInternal();

        if (this.config.showAgentWindows()) {
            fmlSenderGUI = new ActiveMQStatusGUI();
            fmlSenderGUI.setActiveMQWrapper(fmlSender);
            fmlSenderGUI.show();

            agentFeedbackGUI = new ActiveMQStatusGUI();
            agentFeedbackGUI.setActiveMQWrapper(agentFeedback);
            agentFeedbackGUI.show();
        }
    }

    public void close() {
        if (fmlSender != null) {
            fmlSender.close();
            agentFeedback.stop();
        }
    }

    private void initInternal() {
        String url = UrlBuilder.getUrlTcp(iniManager.getValueString("AMQ.host"), iniManager.getValueString("AMQ.port"));

        String fmlSenderTopic = iniManager.getValueString("AMQ.agent.fml.name");
        boolean fmlSenderIsTopic = iniManager.getValueBoolean("AMQ.agent.fml.isTopic");

        String feedbackTopic = iniManager.getValueString("AMQ.agent.feedback.name");
        boolean feedbackIsTopic = iniManager.getValueBoolean("AMQ.agent.feedback.isTopic");

        fmlSender = new SimpleProducerWrapper(url, fmlSenderTopic, fmlSenderIsTopic);
        agentFeedback = new SimpleReceiverWrapper(url, feedbackTopic, feedbackIsTopic);

        fmlSender.init();
        agentFeedback.start(receiver);
    }

    private void onFeedback(Message message) {
        System.out.println(String.valueOf(AgentFeedback.FromJMSMessage(message)));
    }

    public void sendFML(String fml) {
        sendFML(fml, "id" + fmlId++);
    }

    public void sendFML(String fml, String id) {
        TextMessage textMessage = fmlSender.createTextMessage(fml);
        if (textMessage != null) {
            try {
                textMessage.setStringProperty("content-id", id);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            fmlSender.sendMessage(textMessage);
        }
    }
}
