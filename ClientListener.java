import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Vector;

public class ClientListener implements Runnable{
    Socket socket;
    ClientUserInterface GUI;
    public ClientListener(Socket socket, ClientUserInterface GUI) {
        this.socket = socket;
        this.GUI = GUI;
    }

    public void append(Color c, String s) {
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
            int len = GUI.chatLog.getDocument().getLength(); // same value as getText().length();
            GUI.chatLog.setCaretPosition(len);  // place caret at the end (with no selection)
            GUI.chatLog.setCharacterAttributes(aset, false);
//            GUI.chatLog.setEditable(true);
            GUI.chatLog.replaceSelection(s + "\n"); // there is no selection, so inserts at caret
//            GUI.chatLog.setEditable(false);
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverInput;
            while((serverInput = in.readLine()) != null) {
                JScrollBar scrollBar = GUI.chatLogScroll.getVerticalScrollBar();
                String [] split = serverInput.split(" ");
                switch (split[0]) {
                    case "/pong" -> {
                        if (split.length == 2) {
                            Instant pingTime = Instant.parse(split[1]);
                            Instant currTime = Instant.now();
                            String timeBetween = ChronoUnit.MILLIS.between(pingTime, currTime) + "";
                            GUI.chatLog.appendANSI("\u001B[30mPong! (" + timeBetween + " ms)\n");
                        }
                    }
                    case "/refreshusers" -> {
                        int i = 0;
                        DefaultTableModel newTableModel = new DefaultTableModel();
                        newTableModel.addColumn("Users");
                        for (String username : split) {
                            if (!username.equals("/refreshusers")) {
                                String [] tempArray = {username};
                                newTableModel.addRow(tempArray);
                                System.out.println(username);
                            }
                        }
                        GUI.activeUsers.setModel(newTableModel);
                    }
                    default -> {
                        GUI.chatLog.appendANSI(serverInput + "\n");
                    }
                }
                if (scrollBar.getValue() >= scrollBar.getMaximum()-500) {  // auto scroll past a certain point
                    scrollBar.setValue(scrollBar.getMaximum());
                }
            }
        } catch (SocketException se) {
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }
}
