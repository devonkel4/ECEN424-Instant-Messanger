import java.net.*;

public class ServerFileHost implements Runnable{
    int portNum;
    String fileName;
    public ServerFileHost (int portNum, String fileName) {
        this.portNum = portNum;
        this.fileName = fileName;
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(portNum);
            // 1m timeout for user to confirm
            serverSocket.setSoTimeout(60000);

            try {
                while (true) {
                    System.out.println("Accepting sockets");
                    Socket connectionSocket = serverSocket.accept();
                    System.out.println("Accepted a socket");
                    Thread sendFileThread = new Thread(new ServerFileSender(connectionSocket, fileName));
                    sendFileThread.start();
                }
            } catch (SocketTimeoutException ste) {
                System.out.println(ste);
                System.out.println("Socket timed out, closing file server");
                serverSocket.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
