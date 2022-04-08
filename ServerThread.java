import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class ServerThread implements Runnable{
    Socket connectionSocket;
    String messageToSend;

    public ServerThread(Socket connectionSocket, String messageToSend) {
        this.connectionSocket = connectionSocket;
        this.messageToSend = messageToSend;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
            String clientInput;

            out.println(messageToSend);

            // get client ip address and port
            String clientHost = connectionSocket.getInetAddress().toString();
            String clientIp = clientHost.split("/")[1];
            String clientPort = connectionSocket.getPort() + "";

            System.out.printf("%s:%s has connected.\n", clientIp, clientPort);

            while ((clientInput = in.readLine()) != null) {
                // terminate connection if "\\disconnect" is received
                if (clientInput.equals("\\disconnect")) {
                    System.out.printf("%s:%s has disconnected.\n", clientIp, clientPort);
                    connectionSocket.close();
                    break;
                } else {  // otherwise print message
                    System.out.printf("%s:%s: %s\n", clientIp, clientPort, clientInput);
                }
            }

            connectionSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }


}
