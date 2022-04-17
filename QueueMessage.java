import java.net.*;

enum MessageType {
    CONNECT, DISCONNECT, MESSAGE, FILE, EXIT, FUNCTION
}

public class QueueMessage {
    MessageType msgType;
    Socket socket;
    String id;
    String content;

    public QueueMessage(MessageType msgType, Socket socket) {
        this.msgType = msgType;
        this.socket = socket;
    }

    public QueueMessage(MessageType msgType, Socket socket, String content) {
        this.msgType = msgType;
        this.socket = socket;
        this.content = content;
    }

    public QueueMessage(MessageType msgType, String content) {
        this.msgType = msgType;
        this.content = content;
    }
}
