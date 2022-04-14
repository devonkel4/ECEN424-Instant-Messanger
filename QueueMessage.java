import java.net.*;

enum MessageType {
    CONNECT, DISCONNECT, MESSAGE, FILE, EXIT
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
}
