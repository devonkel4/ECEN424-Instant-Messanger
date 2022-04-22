import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class ServerUserInterface {
    private JFrame f;
    private JPanel base;
    private JPanel upperPanel;
    private JPanel lowerPanel;
    private JPanel logPanel;
    private JPanel activePanel;
    public JTextField userInput;
    public ColorPane chatLog;
    public JTable activeUsers;
    private JScrollPane chatLogScroll;
    private JScrollPane activeUsersScroll;
    private LinkedList<Socket> sockets;
    PrintWriter out;

    public ServerUserInterface(LinkedList<Socket> sockets) {

        f = new JFrame("Server");
        base = new JPanel(new BorderLayout());
        upperPanel = new JPanel(new GridLayout());
        lowerPanel = new JPanel(new GridLayout());
        logPanel = new JPanel(new GridLayout());
        activePanel = new JPanel(new GridLayout());
        userInput = new JTextField();
        chatLog = new ColorPane();
        activeUsers = new JTable();
        chatLogScroll = new JScrollPane(chatLog);
        activeUsersScroll = new JScrollPane(activeUsers);

        Font originalFont = userInput.getFont();
        Font resizedFont = new Font("Courier New", originalFont.getStyle(), 16);
        userInput.setFont(resizedFont);
        chatLog.setFont(resizedFont);

        this.sockets = sockets;

        base.add(upperPanel);
        base.add(lowerPanel,BorderLayout.SOUTH);

        upperPanel.add(logPanel);
        upperPanel.add(activePanel);
        lowerPanel.add(userInput);

        logPanel.add(chatLogScroll);
        activePanel.add(activeUsersScroll);

        chatLog.appendANSI("\u001B[30m");
        chatLog.setEditable(false);
        userInput.addKeyListener(keyListener);

        f.add(base);
        f.setSize(1000,500);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String [] args) {
    }

    KeyListener keyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String input = userInput.getText();
                if (!input.equals("")) {
                    for (Socket socket : sockets) {
                        try {
                            out = new PrintWriter(socket.getOutputStream(), true);
                        } catch (Exception err) {
                            err.printStackTrace();
                            System.err.println(err.getClass().getName()+": "+err.getMessage());
                        }
                        out.println("SERVER: " + input);
                    }
                    chatLog.appendANSI("SERVER: " + input + "\n");
                    userInput.setText("");
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };
}
