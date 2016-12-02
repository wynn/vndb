package VNDB;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import static VNDB.VNDBConstants.*;

/**
 * Created by Aria on 12/27/2015.
 */
public class VNDBSession {

    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    private boolean isLoggedIn;

    public VNDBSession() throws IOException {
        this.socket = new Socket("api.vndb.org", 19534);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public boolean login() {
        if (isLoggedIn) return true;

        String loginMessage = "login {\"protocol\":1,\"client\":\"test\",\"clientver\":0.1}";
        if (sendMessage(loginMessage).equals("ok")) {
            isLoggedIn = true;
            return true;
        }
        return false;
    }

    public String sendMessage(String message) {
        try {
            ByteBuffer msg = ByteBuffer.allocate(message.getBytes().length + 1);
            msg.put(message.getBytes());
            msg.put(END_OF_TRANSMISSION_CHAR); //End of transmission character
            outputStream.write(msg.array());

            //todo remember why I allocated this much space
            ByteBuffer messageIncoming = ByteBuffer.allocate(2048);
            inputStream.read(messageIncoming.array());
            byte[] temp = messageIncoming.array();

            //so Japanese strings like いろとりどりのセカイ don't get butchered
            String str = new String(temp, "UTF-8").trim();
            System.out.println("Received: " + str);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}