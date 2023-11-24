package tophat.fun.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import tophat.fun.utilities.math.MathUtil;
import tophat.fun.utilities.player.PrintingUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public interface Methods {

    Minecraft mc = Minecraft.getMinecraft();

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    PrintingUtil chatUtil = new PrintingUtil();

    default boolean isMoving() {
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    default float getBPS() {
        float squareMotion = (float)(MathUtil.square(mc.thePlayer.motionX) + MathUtil.square(mc.thePlayer.motionZ));
        return (float)MathUtil.round(Math.sqrt(squareMotion) * 20.0D * mc.timer.timerSpeed, (int) 2.0D);
    }

    default int getPing() {
        NetworkPlayerInfo networkPlayerInfo = null;
        try {
            networkPlayerInfo = mc.getNetHandler().getPlayerInfoMap().stream().filter(player ->
                            player.getGameProfile().getId().toString().equals(this.mc.getNetHandler().getGameProfile().getId().toString()))
                    .findFirst().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (networkPlayerInfo != null) {
            return networkPlayerInfo.getResponseTime();
        } else {
            return -1;
        }
    }

    default String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    default void jump() { mc.thePlayer.jump(); }

    default void sendPacket(Packet<? extends INetHandler> packet) {
        mc.thePlayer.sendQueue.addToSendQueue(packet);
    }

    default void sendPacketUnlogged(Packet<? extends INetHandler> packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }

    default void sendChat(String message, boolean prefix) {
        chatUtil.addChatMessage(message, prefix);
    }

    default void sendChat(String message) {
        chatUtil.addChatMessage(message, false);
    }

    default double getX() { return mc.thePlayer.posX; }
    default double getY() { return mc.thePlayer.posY; }
    default double getZ() { return mc.thePlayer.posZ; }
    default float getYaw() { return mc.thePlayer.rotationYaw; }
    default float getPitch() { return mc.thePlayer.rotationPitch; }
    default boolean getGround() { return mc.thePlayer.onGround; }

    static void createFolder(String name) {
        Path directoryPath = Paths.get(name);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectory(directoryPath);
            } catch (IOException e) {
                chatUtil.addToConsole("Failed to create the directory: " + name + " due to " + e.getMessage().toLowerCase(Locale.ROOT));
            }
        }
    }


}
