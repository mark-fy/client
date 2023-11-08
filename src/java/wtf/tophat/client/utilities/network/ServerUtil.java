package wtf.tophat.client.utilities.network;

import java.util.HashMap;
import java.util.Map;
import wtf.tophat.client.utilities.Methods;

public final class ServerUtil implements Methods {
    private static final Map<String, Long> serverIpPingCache;
    public static long timeJoined;
    public static String serverIp;

    public static String getSessionLengthString() {
        serverIp = mc.getCurrentServerData() != null ? ServerUtil.mc.getCurrentServerData().serverIP : "Singleplayer";
        long totalSeconds = (System.currentTimeMillis() - timeJoined) / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = totalSeconds % 3600L / 60L;
        long seconds = totalSeconds % 60L;
        return (hours > 0L ? hours + "h " : "") + minutes + "m " + seconds + "s";
    }

    static {
        timeJoined = System.currentTimeMillis();
        serverIpPingCache = new HashMap<String, Long>();
    }
}
