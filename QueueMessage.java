import java.net.*;

enum MessageType {
    CONNECT, DISCONNECT, MESSAGE, FILE, EXIT, FUNCTION
}

public class QueueMessage {
    MessageType msgType;
    User user;
    String content;

    public QueueMessage(MessageType msgType, User user) {
        this.msgType = msgType;
        this.user = user;
    }

    public QueueMessage(MessageType msgType, User user, String content) {
        this.msgType = msgType;
        this.user = user;
        this.content = content;
    }

    public QueueMessage(MessageType msgType, String content) {
        this.msgType = msgType;
        this.content = content;
    }
}
