import java.io.PrintWriter;
import java.net.*;

public class Server {
    public static int isValidPort(String input) {
        int portNum;

        // check if port is an int
        try {portNum = Integer.parseInt(input);}
        catch (NumberFormatException e) {return -1;}

        // check if port is in valid range of an int
        if (portNum < 0 || portNum > 65535) {return -1;}

        return portNum;
    }

    public static int isNumeric(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String [] args) {
        int serverPort = isValidPort(args[0]);
        int maxClients = isNumeric(args[1]);
        String messageToSend = args[2];

        if (serverPort == -1) {
            System.out.println("ERROR: Port number is not valid.");
            System.out.println("Program exiting...");
            System.exit(0);
        }

        if (maxClients == -1) {
            System.out.println("ERROR: Number input for max clients is not valid.");
            System.out.println("Program exiting...");
            System.exit(0);
        }

        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            return;
        }

        Thread [] t = new Thread[maxClients];

        while (true) {
            try {
                Socket connectionSocket = serverSocket.accept();
                boolean atMaxConnections = true;

                // check if there are any open threads
                for (int i = 0; i < maxClients; i++) {
                    // if thread doesn't exist, or if thread exists and has finished running
                    if ( (t[i] == null) || (t[i] != null && !t[i].isAlive()) ) {
                        t[i] = new Thread(new ServerThread(connectionSocket, messageToSend));
                        t[i].start();
                        atMaxConnections = false;
                        break;
                    }
                }

                // refuse client's connection
                if (atMaxConnections) {
                    PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
                    out.println("Connection refused.");
                    System.out.println("Server capacity is full. Connection refused.");
                    connectionSocket.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                return;
            }
        }
    }
}
