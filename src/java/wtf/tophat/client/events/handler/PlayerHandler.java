package wtf.tophat.client.events.handler;

public class PlayerHandler {

    public static float yaw, pitch, prevYaw, prevPitch;
    public static boolean shouldSprintReset, moveFix;
    public static MoveFixMode currentMode;

    public enum MoveFixMode {
        STRICT, SILENT, AGGRESSIVE
    }

}
