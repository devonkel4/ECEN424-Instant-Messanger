import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class ServerListener implements Runnable{
    Socket connectionSocket;
    String messageToSend;
    BlockingQueue<QueueMessage> messageQueue;
    String id;
    ServerUserInterface GUI;
//    static FileWriter fileWriter;
    public ServerListener(Socket connectionSocket, String messageToSend, BlockingQueue<QueueMessage> messageQueue, int i,
                          ServerUserInterface GUI) {
        this.connectionSocket = connectionSocket;
        this.messageToSend = messageToSend;
        this.messageQueue = messageQueue;
        this.GUI = GUI;
        QueueMessage connectMessage = new QueueMessage(MessageType.CONNECT, connectionSocket);
        messageQueue.add(connectMessage);
        id = i + "";
    }

    private void parseFunction(String function) {
        String [] split = function.split(" ");
        String functionType = split[0].substring(1);
        if (functionType.equals("nick")) {
            String functionAnnouncement = id + " has changed their name to " + split[1];
            id = split[1];
            QueueMessage functionMessage = new QueueMessage(MessageType.FUNCTION, functionAnnouncement);
            messageQueue.add(functionMessage);
        }
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
            String clientInput;

            out.println(messageToSend);

            // get client ip address and port
            String clientHost = connectionSocket.getInetAddress().toString();
            String clientIp = clientHost.split("/")[1];
            String clientPort = connectionSocket.getPort() + "";

            System.out.printf("%s:%s has connected.\n", clientIp, clientPort);

            while ((clientInput = in.readLine()) != null) {
                // terminate connection if "\\disconnect" is received
                System.out.println(clientInput);
                try {
                    if (clientInput.equals("\\disconnect")) {
                        System.out.printf("%s:%s has disconnected.\n", clientIp, clientPort);
                        QueueMessage disconnectMessage = new QueueMessage(MessageType.DISCONNECT, connectionSocket);
                        messageQueue.add(disconnectMessage);
                        connectionSocket.close();
                        break;
                    } else if (clientInput.substring(0,1).equals("/")) {
                        parseFunction(clientInput);
                    } else {  // otherwise print message
                        QueueMessage stringMessage = new QueueMessage(MessageType.MESSAGE, connectionSocket, clientInput);
                        stringMessage.id = id;
                        messageQueue.add(stringMessage);
                        System.out.printf("%s:%s: %s\n", clientIp, clientPort, clientInput);
//                        fileWriter.write("<" + clientIp + ":" + clientPort + "> " + clientInput + '\n');

                    }
                } catch (SocketException se) {
                    System.out.printf("%s:%s has disconnected.\n", clientIp, clientPort);
                    QueueMessage disconnectMessage = new QueueMessage(MessageType.DISCONNECT, connectionSocket);
                    messageQueue.add(disconnectMessage);
                    connectionSocket.close();
                }
            }
            connectionSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }


}
