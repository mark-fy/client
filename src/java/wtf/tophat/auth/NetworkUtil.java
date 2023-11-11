package wtf.tophat.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class NetworkUtil {

    public static String getRawContent() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/spltiz1337/db/main/test.th?token=GHSAT0AAAAAACJQBA75A3FERGZPRT5ZWJNAZKO6ILA");
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
                content.append("\n");
            }
            in.close();

            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getUsernameFromHWID(String targetHWID) {
        if (getRawContent() != null && targetHWID != null) {
            String[] lines = getRawContent().split("\n");

            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String username = parts[0];
                    String hwid = parts[1];
                    if (hwid.equals(targetHWID)) {
                        return username;
                    }
                }
            }
        }

        return "null";
    }

    public static String getUIDFromHWID(String targetHWID) {
        if (getRawContent() != null && targetHWID != null) {
            String[] lines = getRawContent().split("\n");

            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String hwid = parts[1];
                    String uid = parts[2];
                    if (hwid.equals(targetHWID)) {
                        return uid;
                    }
                }
            }
        }

        return "null";
    }

    public static boolean doesHWIDExistInContent(String targetHWID) {
        if (getRawContent() != null && targetHWID != null) {
            String[] lines = getRawContent().split("\n");

            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String hwid = parts[1];
                    if (hwid.equals(targetHWID)) {
                        return true; // HWID exists in the content
                    }
                }
            }
        }

        return false;
    }

}
