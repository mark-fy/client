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
            URL url = new URL("https://tophat.fun/assets/test.th");
            URLConnection connection = url.openConnection();

            if (connection.getURL().getHost().equals("localhost")) {
                System.out.println("Blocked connection from localhost.");
                return null; // or handle the blocking logic as needed
            }

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

    // parts[0] = username
    // parts[1] = hwid
    // parts[2] = uid
    // parts[3] = key
    // parts[4] = discord-id

    public static String getUsernameFromHWID(String targetHWID) {
        if (getRawContent() != null && targetHWID != null) {
            String[] lines = getRawContent().split("\n");

            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 5) {
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
                if (parts.length == 5) {
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
                if (parts.length == 5) {
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
