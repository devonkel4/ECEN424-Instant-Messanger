import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

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
        User.userList = new ArrayList<User>(1);
        BlockingQueue<QueueMessage> messageQueue = new LinkedBlockingDeque<>();

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
        LinkedList<User> users = new LinkedList<>();
        ServerUserInterface GUI = new ServerUserInterface(users);

        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            return;
        }

//        String fName = Date.from(Instant.now()) + ".txt";
//        File file = new File(fName);
//        ServerListener.fileWriter = new FileWriter(fName);

        Thread [] t = new Thread[maxClients];
        Thread t2 = new Thread(new ServerBroadcaster(messageQueue, GUI, users));
        t2.start();
        while (true) {
            try {
                Socket connectionSocket = serverSocket.accept();
                boolean atMaxConnections = true;

                // check if there are any open threads
                for (int i = 0; i < maxClients; i++) {
                    // if thread doesn't exist, or if thread exists and has finished running
                    if ( (t[i] == null) || (t[i] != null && !t[i].isAlive()) ) {

                        // TODO: authenticate users
                        User currUser = null;
                        BufferedReader tempIn = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        String authStr = tempIn.readLine();
                        String[] authSplit = authStr.split(" ");
                        boolean found = false;
                        boolean authenticated = false;
                        for(User user : User.userList){
                            if (user.getUsername().equals(authSplit[0])){
                                found = true;
                                authenticated = user.verifyPW(authSplit[1]);
                                currUser = user;
                                break;
                            }
                        }
                        if (!found){
//                            users.addLast(new User(authSplit[0], authSplit[1]));
                            currUser = new User(authSplit[0], authSplit[1]);
                            User.userList.add(currUser);
                            authenticated = true;
                        }
                        if (!authenticated){
                            PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
                            out.println("Connection refused invalid login.");
                            System.out.println("Server. Bad login");
                            connectionSocket.close();
                            atMaxConnections = false;
                            break;
                        }
                        users.addLast(currUser); // adds no matter whether its new or old to active
                        for (User user : users) {
                            // ban list is null, commented out for testing
//                            for (int j = 0; i < user.getBanList().size(); ++i){
                            for (int j = 0; i < 0; ++i){
                                if (connectionSocket.getInetAddress().toString().equals(user.getBanList().get(j))) {
                                    PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
                                    out.println("Connection refused Banned user.");
                                    System.out.println("Server. Banned user");
                                    connectionSocket.close();
                                }
                            }
                        }
                        //User user = new User(i + "", i + "");
                        currUser.setSocket(connectionSocket);
                        t[i] = new Thread(new ServerListener(currUser, "OK", messageQueue, i, GUI));
                        t[i].start();
                        atMaxConnections = false;
                        break;
                    }
                }

                // refuse client's connection
                if (atMaxConnections) {
                    PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
                    out.println("Connection refused.");
                    System.out.println("Server.Server capacity is full. Connection refused.");
                    connectionSocket.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
//                ServerListener.fileWriter.close();
                return;
            }
        }
    }
}
