package wtf.tophat.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.util.Timer;
import wtf.tophat.Client;
import wtf.tophat.utilities.player.chat.ChatUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface Methods {

    Minecraft mc = Minecraft.getMinecraft();

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    default EntityPlayerSP getPlayer() { return mc.player; }
    default Timer getMCTimer() { return mc.timer; }
    default WorldClient getWorld() { return mc.world; }

    static boolean isMoving() { return mc.player.moveForward != 0 || mc.player.moveStrafing != 0; }

    default void sendPacket(Packet<? extends INetHandler> packet) { mc.player.sendQueue.send(packet); }

    default void sendPacketUnlogged(Packet<? extends INetHandler> packet) { mc.getNetHandler().getNetworkManager().sendPacket(packet); }

    default void sendChat(String message, boolean prefix) { ChatUtil.addChatMessage(message, prefix); }

    default void sendChat(String message) { ChatUtil.addChatMessage(message, false); }

    default void click(boolean doubleClick) {
        mc.click();
        if(doubleClick)
            mc.click();
    }

    static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    default double getX() { return mc.player.posX; }
    default double getY() { return mc.player.posY; }
    default double getZ() { return mc.player.posZ; }
    static float getYaw() { return mc.player.rotationYaw; }
    default float getPitch() { return mc.player.rotationPitch; }
    default boolean getGround() { return mc.player.onGround; }

    default boolean canClimbWall() {
        return getPlayer() != null && getPlayer().isCollidedHorizontally && !getPlayer().isOnLadder() && !getPlayer().isInWater() && getPlayer().fallDistance < 1.0F;
    }

    default boolean getDead() { return mc.player.isDead; }

     static void createFolder(String name) {
        Path directoryPath = Paths.get(name);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectory(directoryPath);
            } catch (IOException e) {
                e.printStackTrace();
                Client.printL("Failed to create the directory.");
            }
        }
    }
}
