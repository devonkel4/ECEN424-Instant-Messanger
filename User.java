import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

public class User {
    private static ArrayList<User> userList;
    public static ArrayList<String> banList; //will either hold ips or unames

    private Socket socket;
    private String hashedPassword;
    private String username;
    private boolean banned;
    private Color nameColor;
    private Color textColor;
    private Color bgColor;
    private ArrayList<String> ipLog; //Maybe Unused depending on implementation of banning

    public User(String username, String password) throws NoSuchAlgorithmException {
        this.username = username;
        setPassword(password);
        banned = false;
        nameColor = Color.NONE;
        textColor = Color.NONE;
        bgColor = Color.NONE;
        ipLog = new ArrayList<>(1);
    }

    private void setPassword(String password) throws NoSuchAlgorithmException {
        hashedPassword = HashPass(password);
    }

    //TODO: Implement Change PW function that verifies old password and sets new password if passed
    //DOC: Returns whether the password was changed
//    public boolean changePassword(String old, String password){
//
//    }
    public boolean verifyPW(String password) throws NoSuchAlgorithmException {
        return hashedPassword.equals(HashPass(password));
    }

    private String HashPass(String password) throws NoSuchAlgorithmException { //SHA-256 Hash of the password
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        BigInteger num = new BigInteger(1, hash);
        StringBuilder build = new StringBuilder(num.toString(16));
        while(build.length() < 32)
            build.insert(0, '0');
        return build.toString();
    }
    public String FormatMessage(String message){ //Returns Message with color settings applied
        return "<" + nameColor + username + Color.RESET + "> " + bgColor + textColor + message + Color.RESET;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username) {this.username = username;}
    public Socket getSocket() { return socket; }
    public void setSocket(Socket socket) { this.socket = socket; }
    public Color getNameColor() { return nameColor; };
    public Color getTextColor() { return textColor; }
    public void setNameColor(Color color) { nameColor = color; }
    public void setTextColor(Color color) { textColor = color; }
    public List<String> getBanList() {return banList;}
}
