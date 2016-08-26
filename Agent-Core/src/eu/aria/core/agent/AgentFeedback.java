package eu.aria.core.agent;

import javax.jms.Message;

/**
 *
 * Created by adg on 21/08/2016.
 */
public class AgentFeedback {

    public enum Type {
        Start("start"), End("end"), Other("__");

        private String name;

        Type(String value) {
            this.name = value;
        }

        public static Type FromString(String name) {
            for (Type value : values()) {
                if (value.name.equalsIgnoreCase(name)) {
                    return value;
                }
            }
            return Other;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private String id;
    private String typeString;
    private Type type;
    private long timeStamp;

    private AgentFeedback() {
    }

    public String getId() {
        return id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Type getType() {
        return type;
    }

    public String getTypeString() {
        return typeString;
    }

    @Override
    public String toString() {
        return "{ AgentFeedback: id: " + id + ", type: " + typeString + ", time: " + timeStamp + " }";
    }

    public static AgentFeedback FromJMSMessage(Message message) {
        try {
            AgentFeedback agentFeedback = new AgentFeedback();
            agentFeedback.id = message.getStringProperty("feedback-id");
            agentFeedback.typeString = message.getStringProperty("feedback-type");
            agentFeedback.type = Type.FromString(agentFeedback.typeString);
            agentFeedback.timeStamp = message.getLongProperty("feedback-time");
            return agentFeedback;
        } catch (Exception e) {
            System.err.println("Could not get agent feedback from activemq message!");
            e.printStackTrace();
        }
        return null;
    }
}
