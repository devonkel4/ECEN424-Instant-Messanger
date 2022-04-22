import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.BlockingQueue;

public class ServerListener implements Runnable{
    User user;
    String messageToSend;
    BlockingQueue<QueueMessage> messageQueue;
    ServerUserInterface GUI;
//    static FileWriter fileWriter;
    public ServerListener(User user, String messageToSend, BlockingQueue<QueueMessage> messageQueue, int i,
                          ServerUserInterface GUI) {
        this.user = user;
        this.messageToSend = messageToSend;
        this.messageQueue = messageQueue;
        this.GUI = GUI;
        QueueMessage connectMessage = new QueueMessage(MessageType.CONNECT, user);
        messageQueue.add(connectMessage);
    }

    private void parseFunction(String function) {
        String [] split = function.split(" ");
        String functionType = split[0].substring(1);
        switch(functionType) {
            case "nick" -> {
                if (split.length > 1) {
                    String functionAnnouncement = user.getUsername() + " has changed their name to " + split[1];
                    try {
                        user.setUsername(split[1]);
                    } catch (Exception e) {

                    }
                    QueueMessage functionMessage = new QueueMessage(MessageType.FUNCTION, functionAnnouncement);
                    messageQueue.add(functionMessage);
                    QueueMessage refreshMessage = new QueueMessage(MessageType.FUNCTION, "/refreshusers ");
                    messageQueue.add(refreshMessage);
                }
            }
            case "ping" -> {
                String functionAnnouncement = "/ping " + split[1];
                QueueMessage functionMessage = new QueueMessage(MessageType.FUNCTION, functionAnnouncement);
                messageQueue.add(functionMessage);
            }
            case "refreshusers" -> {
                String functionAnnouncement = "/refreshusers ";
                QueueMessage functionMessage = new QueueMessage(MessageType.FUNCTION, functionAnnouncement);
                messageQueue.add(functionMessage);
            }
            case "w" -> {
                String functionAnnouncement = function;

                QueueMessage functionMessage = new QueueMessage(MessageType.FUNCTION, user, functionAnnouncement);
                messageQueue.add(functionMessage);
            }
            case "namecolor" -> {
                if (split.length > 1) {
                    Color color = Color.valueOf(split[1]);
                    user.setNameColor(color);
                }
            }
            case "textcolor" -> {
                if (split.length > 1) {
                    Color color = Color.valueOf(split[1]);
                    user.setTextColor(color);
                }
            }
        }
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(user.getSocket().getInputStream()));
            PrintWriter out = new PrintWriter(user.getSocket().getOutputStream(), true);
            String clientInput;

            out.println(messageToSend);

            // get client ip address and port
            String clientHost = user.getSocket().getInetAddress().toString();
            String clientIp = clientHost.split("/")[1];
            String clientPort = user.getSocket().getPort() + "";

            System.out.printf("%s:%s has connected.\n", clientIp, clientPort);

            // terminate connection if "\\disconnect" is received
            try {
            while ((clientInput = in.readLine()) != null) {
                    if (clientInput.equals("\\disconnect")) {
                        System.out.printf("%s:%s has disconnected.\n", clientIp, clientPort);
                        QueueMessage disconnectMessage = new QueueMessage(MessageType.DISCONNECT, user);
                        messageQueue.add(disconnectMessage);
                        user.getSocket().close();
                        break;
                    } else if (clientInput.charAt(0) == '/') {
                        parseFunction(clientInput);
                    } else {  // otherwise print message
                        QueueMessage stringMessage = new QueueMessage(MessageType.MESSAGE, user, clientInput);
                        messageQueue.add(stringMessage);
                        System.out.printf("%s:%s: %s\n", clientIp, clientPort, clientInput);
//                        fileWriter.write("<" + clientIp + ":" + clientPort + "> " + clientInput + '\n');

                    }

                }
            } catch (SocketException se) {
                System.out.printf("%s:%s has disconnected.\n", clientIp, clientPort);
                QueueMessage disconnectMessage = new QueueMessage(MessageType.DISCONNECT, user);
                messageQueue.add(disconnectMessage);
                user.getSocket().close();
            }
            user.getSocket().close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }


}
