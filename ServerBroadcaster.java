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
                        sendMessage("\u001B[31m" + currentMessage.user.getUsername() + ": \u001B[30m" + currentMessage.content);
                     }

                     case CONNECT -> {
                         // handle connect
                         users.add(currentMessage.user);
                     }

                     case DISCONNECT -> {
                         // handle disconnect
                         users.remove(currentMessage.user);
                     }

                     case FILE -> {
                         // TODO: serve files
                     }

                     case FUNCTION -> {
                         String [] split = currentMessage.content.split(" ");
                         switch(split[0]) {
                             case "/refreshusers" -> {
                                 // TODO: get users
                             }
                             default -> {
                                 sendMessage(currentMessage.content);
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
