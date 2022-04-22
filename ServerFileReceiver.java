import java.io.*;
import java.net.Socket;
import java.util.Queue;

public class ServerFileReceiver implements Runnable{

    String hostName;
    int portNum;
    String fileName;
    String dirName = "./received/";
    Socket clientSocket;
    Queue<QueueMessage> messageQueue;

    InputStream inFromServer;
    OutputStream outToServer;
    BufferedInputStream bis;
    PrintWriter pw;
    int size = 9022386;

    public ServerFileReceiver(String hostName, int portNum, String fileName, Queue<QueueMessage> messageQueue) {
        this.hostName = hostName;
        this.portNum = portNum;
        this.fileName = fileName;
        this.messageQueue = messageQueue;
    }

    public void run() {
        try {
            clientSocket = new Socket(hostName, portNum);
            inFromServer = clientSocket.getInputStream();
            pw = new PrintWriter(clientSocket.getOutputStream(), true);
            outToServer = clientSocket.getOutputStream();
            ObjectInputStream oin = new ObjectInputStream(inFromServer);

            File f = new File(dirName, fileName);
            FileOutputStream fileOut = new FileOutputStream(f);
            DataOutputStream dataOut = new DataOutputStream(fileOut);
            boolean complete = true;
            int c;
            byte[] data = new byte[size];

            // program receives 4 characters at the start of file transfer, unsure what it is
            byte[] throwawaybuf = new byte[4];
            inFromServer.read(throwawaybuf, 0, 4);
            //empty file case
            while (complete) {
                c = inFromServer.read(data, 0, data.length);
                if (c == -1) {
                    complete = false;
                    System.out.println("Completed");
                } else {
                    dataOut.write(data, 0, c);
                    dataOut.flush();
                }
            }
            fileOut.close();
            QueueMessage broadcastFileMessage = new QueueMessage(MessageType.FILE, fileName);
            messageQueue.add(broadcastFileMessage);
            clientSocket.close();
        } catch (Exception exc) {
            System.out.println("Exception: " + exc.getMessage());
        }
    }


}
