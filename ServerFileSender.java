import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFileSender implements Runnable{
    Socket socket;
    String filename;
    String dirName = "./received/";
    public ServerFileSender(Socket socket, String filename) {
        this.socket = socket;
        this.filename = filename;
    }

    public void run() {
        filename = dirName + filename;
        boolean fileExists = true;
        System.out.println("Sending of " + filename + " begins");
        try {
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
                System.out.println("Sending begins...");
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
                socket.close();
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
