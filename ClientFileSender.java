import java.io.*;
import java.net.*;

public class ClientFileSender implements Runnable{
    int portNum;
    String filename;
    public ClientFileSender(int portNum, String filename) {
        this.portNum = portNum;
        this.filename = filename;
    }

    public void run() {
        ServerSocket serverSocket;
        boolean fileExists = true;
        try {
            serverSocket = new ServerSocket(portNum);
            // if no response within 1 minute, terminate
            serverSocket.setSoTimeout(60000);
            Socket socket = serverSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            InputStream inFromClient = socket.getInputStream();
            PrintWriter outPw = new PrintWriter(socket.getOutputStream());
            OutputStream output = socket.getOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(output);

            FileInputStream file = null;
            BufferedInputStream bis = null;

            try {
                file = new FileInputStream(filename);
                bis = new BufferedInputStream(file);
            }  catch (FileNotFoundException excep) {
                fileExists = false;
                System.out.println("FileNotFoundException:" + excep.getMessage());
            }

            if (fileExists) {
                oout = new ObjectOutputStream(output);
                System.out.println("Download begins...");
                sendBytes(bis, output);
                System.out.println("Completed");
                bis.close();
                file.close();
                oout.close();
                output.close();
            } else {
                oout = new ObjectOutputStream(output);
                oout.writeObject("FileNotFound");
                bis.close();
                file.close();
                oout.close();
                output.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }

    }

    private static void sendBytes(BufferedInputStream in , OutputStream out) throws Exception {
        int size = 9022386;
        byte[] data = new byte[size];
        int bytes = 0;
        int c = in .read(data, 0, data.length);
        out.write(data, 0, c);
        out.flush();
    }
}
