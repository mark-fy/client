package wtf.tophat.auth;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class HwidUtil {

    public static String getHWID() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    // Create an MD5 hash of the MAC address
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] hash = md.digest(mac);

                    // Convert the hash to a hexadecimal string
                    StringBuilder hwid = new StringBuilder();
                    for (byte b : hash) {
                        hwid.append(String.format("%02X", b));
                    }
                    return hwid.toString();
                }
            }
        } catch (SocketException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

}
