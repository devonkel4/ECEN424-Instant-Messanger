import java.net.*;
import java.io.*;

public class ClientListener implements Runnable{
    Socket socket;
    ClientUserInterface GUI;
    public ClientListener(Socket socket, ClientUserInterface GUI) {
        this.socket = socket;
        this.GUI = GUI;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverInput;
            while((serverInput = in.readLine()) != null) {
                GUI.chatLog.append(serverInput + "\n");
            }
        } catch (SocketException se) {
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }
}
