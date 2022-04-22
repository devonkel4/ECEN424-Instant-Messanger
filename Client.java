import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    public static String isValidIpAddress(String input) {
        String [] splitInput = input.split("\\.");

        if (input.equals("localhost")) return input;
        // check length of ip address
        if (splitInput.length != 4) return "INVALID";

        // check each octet in ip address
        for (String s : splitInput) {
            int ipOctet;

            // check if octet is an integer
            try {
                ipOctet = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return "INVALID";
            }

            // check if octet is within valid range
            if (ipOctet > 255 || ipOctet < 0) return "INVALID";
        }
        return input;
    }

    public static int isValidPort(String input) {
        int portNum;

        // check if port is an int
        try {portNum = Integer.parseInt(input);}
        catch (NumberFormatException e) {return -1;}

        // check if port is in valid range of an int
        if (portNum < 0 || portNum > 65535) {return -1;}

        return portNum;
    }

    public static void main(String [] args) {
        String hostName = isValidIpAddress(args[0]);
        int portNumber = isValidPort(args[1]);

        if (hostName.equals("INVALID")) {
            System.out.println("ERROR: Entered IP address is not valid.");
            System.out.println("Program exiting...");
            System.exit(0);
        }

        if (portNumber == -1) {
            System.out.println("ERROR: Entered port number is not valid.");
            System.out.println("Program exiting...");
            System.exit(0);
        }

        Socket clientSocket;

        try {
            PrintWriter output;
            clientSocket = new Socket(hostName, portNumber);
            ClientUserInterface GUI = new ClientUserInterface(clientSocket);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            String username = JOptionPane.showInputDialog(null, "Enter User Name:");
            output.println(username);
            String password = JOptionPane.showInputDialog(null, "Enter Password");
            output.println(password);
            output.flush();


            String serverMessage = in.readLine();
            System.out.println(serverMessage);

            if (!serverMessage.equals("Connection refused.")) {
                Thread t = new Thread(new ClientListener(clientSocket, GUI));
                t.start();
            }
            else{
                GUI.chatLog.appendANSI(Color.BLACK + serverMessage + "\n");
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }

    }
}
