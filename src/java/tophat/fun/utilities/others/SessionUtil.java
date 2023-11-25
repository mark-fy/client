package tophat.fun.utilities.others;

import tophat.fun.utilities.Methods;

public class SessionUtil implements Methods {

    public static long timeJoined;
    public static String serverIp;

    public static String getSessionLength() {
        serverIp = mc.getCurrentServerData() != null ? mc.getCurrentServerData().serverIP : "Singleplayer";
        long totalSeconds = (System.currentTimeMillis() - timeJoined) / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = totalSeconds % 3600L / 60L;
        long seconds = totalSeconds % 60L;
        return (hours > 0L ? hours + "h " : "") + minutes + "m " + seconds + "s";
    }

    static {
        timeJoined = System.currentTimeMillis();
    }

}
