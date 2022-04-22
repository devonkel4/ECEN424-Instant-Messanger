import javax.swing.table.DefaultTableModel;
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
    int portNum = 12355;
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
                         // hardcoded port right now, make it be based off something else in the future
                         // change port number so multiple files don't collide with each other
                         portNum += 1;
                        System.out.println("Server finished receiving, sending content to users");
                        Thread sendFileHost = new Thread(new ServerFileHost(portNum, currentMessage.content));
                        sendFileHost.start();
                        sendMessage("/filereceive " + portNum + " " + currentMessage.content);
                     }

                     case FUNCTION -> {
                         String [] split = currentMessage.content.split(" ");
                         switch(split[0]) {
                             case "/refreshusers" -> {
                                 DefaultTableModel newTableModel = new DefaultTableModel();
                                 newTableModel.addColumn("Users");
                                 for (User user : users) {
                                     String [] tempArray = {user.getUsername()};
                                     newTableModel.addRow(tempArray);
                                     currentMessage.content += user.getUsername() + " ";
                                 }

                                 GUI.activeUsers.setModel(newTableModel);
                                 sendMessage(currentMessage.content);
                             }
                             case "/w" ->{
                                 if (split.length < 3){
                                     return;
                                 }
                                 String whisperMessage = "";
                                 for (int j = 2; j < split.length; ++j) {
                                     whisperMessage += split[j];
                                     whisperMessage += " ";
                                 }
                                 String whole = "\u001B[32m" + currentMessage.user.getUsername() + " whispers " + whisperMessage;
                                 for (User user: users){
                                     if (user.getUsername().equals(split[1])){

                                         PrintWriter out = new PrintWriter(user.getSocket().getOutputStream(), true);
                                         out.println(whole);
                                         GUI.chatLog.appendANSI(whole + "\n");
                                     }
                                 }
                             }
                             case "/pong" -> {
                                 PrintWriter out = new PrintWriter(currentMessage.user.getSocket().getOutputStream(), true);
                                 out.println(currentMessage.content);
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
