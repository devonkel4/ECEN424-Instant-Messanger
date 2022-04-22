import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.io.File;
import java.io.FileWriter;
import java.net.*;
import java.io.*;

public class ServerBroadcaster implements Runnable{
    BlockingQueue<QueueMessage> queue;
    ServerUserInterface GUI;
    LinkedList<User> users;
    public ServerBroadcaster(BlockingQueue<QueueMessage> queue, ServerUserInterface GUI, LinkedList<User> users) {
        this.queue = queue;
        this.GUI = GUI;
        this.users = users;
    }

    // make all function announcements blue
    public String prependColor(String string) {
        return Color.BLUE + string;
    }

    public void sendMessage(String input) {
        try {
            for (User user: users) {
                PrintWriter out = new PrintWriter(user.getSocket().getOutputStream(), true);
                out.println(input);
            }
            GUI.chatLog.appendANSI(input + "\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }

    }

    public void run() {

        while (true) {
            try {
                QueueMessage currentMessage = queue.take();
                 switch(currentMessage.msgType) {
                     case MESSAGE -> {
                         // broadcast messages
                         User currUser = currentMessage.user;
                        sendMessage(currUser.getNameColor() + currUser.getUsername() + ": " + currUser.getTextColor() + currentMessage.content);
                     }

                     case CONNECT -> {
                         // handle connect
                         users.add(currentMessage.user);
                     }

                     case DISCONNECT -> {
                         // handle disconnect
                         users.remove(currentMessage.user);
                         QueueMessage refreshMessage = new QueueMessage(MessageType.FUNCTION, "/refreshusers ");
                         queue.add(refreshMessage);
                     }

                     case FILE -> {
                         // TODO: serve files
                     }

                     case FUNCTION -> {
                         String [] split = currentMessage.content.split(" ");
                         switch(split[0]) {
                             case "/refreshusers" -> {
                                 // TODO: get users
                                 for (User user : users) {
                                     currentMessage.content += user.getUsername() + " ";
                                 }
                                 sendMessage(currentMessage.content);
                             }
                             default -> {
                                 sendMessage(prependColor(currentMessage.content));
                             }
                         }
                     }
                     case EXIT -> {
                         System.exit(0);
                     }
                 }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                return;
            }
        }
    }
}
