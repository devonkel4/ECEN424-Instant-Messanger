import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;

public class ClientUserInterface {
    private JFrame f;
    private JPanel base;
    private JPanel upperPanel;
    private JPanel lowerPanel;
    private JPanel logPanel;
    private JPanel activePanel;
    public JTextField userInput;
    public ColorPane chatLog;
    public JTable activeUsers;
    public JScrollPane chatLogScroll;
    private JScrollPane activeUsersScroll;
    private Socket socket;
    PrintWriter out;

    public ClientUserInterface(Socket socket) {

        f = new JFrame("Client");
        base = new JPanel(new BorderLayout());
        upperPanel = new JPanel();

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

        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

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
        //chatLog.setEditable(false);
        userInput.addKeyListener(keyListener);


        f.add(base);
        f.setSize(1250,650);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        new Thread(() -> {
            try {
                Thread.sleep(500);
                out.println("/refreshusers");
            } catch (Exception e) {

            }
        }).start();
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
                if (input.equals("/ping")) {
                    out.println("/ping " + Instant.now().toString());
                    userInput.setText("");
                } else if (input.equals("/help")) {
                    userInput.setText("");
                    String helpText = Color.BLACK + "--------------------------------------\n" +
                            "Commands:\n" +
                            "/namecolor <color name>\t\tChange the color of your name to all users\n" +
                            "/textcolor <color name>\t\tChange the color of text you type to all users\n" +
                            "/refreshusers \t\t\t\tForce refresh user list\n" +
                            "/ping \t\t\t\t\tPing the server and see latency\n" +
                            "/w <user>\t\t\t\tWhisper a user\n" +
                            "--------------------------------------\n";
                    chatLog.appendANSI(helpText);
                } else if (!input.equals("")) {
                    out.println(input);
                    userInput.setText("");
                }

            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };
}
