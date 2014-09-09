package echoclient;

import echoserver.ClientHandler;

/**
 *
 * @author jakobgaardandersen
 */
public class Message {

    private final String message;
    private final String sender;
    private final String textMsg;
    private final MessageType type;
    private final ClientHandler source;

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
    
    public String[] getOnlineUsers(){
        return message.split(",");
    }

    public Message(ClientHandler source, String sender, String textMsg, MessageType type) {
        this.source = source;
        this.textMsg = textMsg;
        this.message = textMsg;
        this.sender = sender;
        this.type = type;
    }
}
