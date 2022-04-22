import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
            GUI.chatLog.setEditable(true);
            GUI.chatLog.replaceSelection(s + "\n"); // there is no selection, so inserts at caret
            GUI.chatLog.setEditable(false);
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverInput;
            while((serverInput = in.readLine()) != null) {
                JScrollBar scrollBar = GUI.chatLogScroll.getVerticalScrollBar();
                String [] split = serverInput.split(" ");
                if (split.length == 2 && split[0].equals("/pong")) {
                    Instant pingTime = Instant.parse(split[1]);
                    Instant currTime = Instant.now();
                    String timeBetween = ChronoUnit.MILLIS.between(pingTime, currTime) + "";
                    GUI.chatLog.appendANSI("\u001B[30mPong! (" + timeBetween + " ms)\n");
                } else {
                    GUI.chatLog.appendANSI(serverInput + "\n");
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
