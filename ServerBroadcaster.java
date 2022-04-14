import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.net.*;
import java.io.*;

public class ServerBroadcaster implements Runnable{
    BlockingQueue<QueueMessage> queue;
    LinkedList<Socket> sockets;
    public ServerBroadcaster(BlockingQueue<QueueMessage> queue) {
        this.queue = queue;
        sockets = new LinkedList<Socket>();

    }

    public void run() {
        while (true) {
            try {
                QueueMessage currentMessage = queue.take();
                 switch(currentMessage.msgType) {
                     case MESSAGE -> {
                         // broadcast messages
                        for (Socket socket : sockets) {
                            if (currentMessage.socket != socket) {
                                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                                out.println(currentMessage.id + ": " + currentMessage.content);
                            }
                        }
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
