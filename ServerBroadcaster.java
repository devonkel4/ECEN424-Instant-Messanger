import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.net.*;
import java.io.*;

public class ServerBroadcaster implements Runnable{
    BlockingQueue<QueueMessage> queue;
    ServerUserInterface GUI;
    LinkedList<Socket> sockets;
    public ServerBroadcaster(BlockingQueue<QueueMessage> queue, ServerUserInterface GUI, LinkedList<Socket> sockets) {
        this.queue = queue;
        this.GUI = GUI;
        this.sockets = sockets;
    }

    public void sendMessage(String input) {
        try {
            for (Socket socket : sockets) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(input);
            }
            GUI.chatLog.append(input + "\n");
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
                        sendMessage(currentMessage.id + ": " + currentMessage.content + "\n");
                     }

                     case CONNECT -> {
                         // handle connect
                         sockets.add(currentMessage.socket);
                     }

                     case DISCONNECT -> {
                         // handle disconnect
                         sockets.remove(currentMessage.socket);
                     }

                     case FILE -> {
                         // TODO: serve files
                     }

                     case FUNCTION -> {
                         sendMessage(currentMessage.content);
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
