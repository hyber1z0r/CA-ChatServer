package echoclient;


/**
 *
 * @author jakobgaardandersen
 */
public class Message {

    private final String message;
    private final String sender;
    private final String textMsg;
    private final MessageType type;
    private final Object source;

    public String getMessage() {
        return textMsg;
    }

    public String getSender() {
        return sender;
    }

    public String getTextMsg() {
        return textMsg;
    }

    public MessageType getType() {
        return type;
    }

    public String[] getOnlineUsers() {
        return message.split(",");
    }

    public Message(Object source, String textMsg, MessageType type) {
        this.source = source;
        this.sender = "server";
        this.type = type;
        this.message = textMsg;
        this.textMsg = textMsg;
    }

    public Message(Object source, String sender, String textMsg, MessageType type) {
        this.source = source;
        this.textMsg = textMsg;
        this.message = textMsg;
        this.sender = sender;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" + "message=" + message + ", sender=" + sender + ", textMsg=" + textMsg + ", type=" + type + ", source=" + source + '}';
    }
    
    
}
