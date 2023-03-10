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
    private LinkedList<User> users;
    PrintWriter out;

    public ServerUserInterface(LinkedList<User> users) {

        f = new JFrame("Server");
        base = new JPanel(new BorderLayout());
        upperPanel = new JPanel(new GridLayout());
        upperPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

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

        this.users = users;

        base.add(upperPanel);
        base.add(lowerPanel,BorderLayout.SOUTH);

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 3;
        c.weightx = 3;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        upperPanel.add(logPanel, c);

        c.weightx = 0;
        c.gridwidth = 1;
        c.gridx = 3;
        c.gridy = 0;


        upperPanel.add(activePanel, c);
        lowerPanel.add(userInput);

        logPanel.add(chatLogScroll);
        activePanel.add(activeUsersScroll);

        chatLog.appendANSI("\u001B[30m");
//        chatLog.setEditable(false);
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
                    for (User user : users) {
                        try {
                            out = new PrintWriter(user.getSocket().getOutputStream(), true);
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
