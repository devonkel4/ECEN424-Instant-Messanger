import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.Random;

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
                Random rnd = new Random();
                int rng = rnd.nextInt();
                GUI.chatLog.appendANSI(serverInput + "\n");
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
