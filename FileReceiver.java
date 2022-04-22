import java.net.Socket;
import java.io.*;

public class FileReceiver implements Runnable{

    String hostName;
    int portNum;
    String fileName;
    String dirName = "./received/";
    Socket clientSocket;
    InputStream inFromServer;
    OutputStream outToServer;
    BufferedInputStream bis;
    PrintWriter pw;
    int size = 9022386;

    public FileReceiver(String hostName, int portNum, String fileName) {
        this.hostName = hostName;
        this.portNum = portNum;
        this.fileName = fileName;
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
        } catch (Exception exc) {
            System.out.println("Exception: " + exc.getMessage());
        }
    }


}
