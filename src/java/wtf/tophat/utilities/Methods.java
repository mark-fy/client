package wtf.tophat.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import wtf.tophat.utilities.chat.ChatUtil;

public interface Methods {

    Minecraft mc = Minecraft.getMinecraft();

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    default EntityPlayerSP getPlayer() { return mc.player; }
    default WorldClient getWorld() { return mc.world; }

    default boolean isMoving() { return mc.player.moveForward != 0 || mc.player.moveStrafing != 0; }

    default void sendPacket(Packet<? extends INetHandler> packet) { mc.player.sendQueue.send(packet); }

    default void sendPacketUnlogged(Packet<? extends INetHandler> packet) { mc.getNetHandler().getNetworkManager().sendPacket(packet); }

    default void sendChat(String message, boolean prefix) { ChatUtil.addChatMessage(message, prefix); }

    default void sendChat(String message) { ChatUtil.addChatMessage(message, false); }

    default void click(boolean doubleClick) {
        mc.click();
        if(doubleClick)
            mc.click();
    }

    default double getX() { return mc.player.posX; }
    default double getY() { return mc.player.posY; }
    default double getZ() { return mc.player.posZ; }
    default float getYaw() { return mc.player.rotationYaw; }
    default float getPitch() { return mc.player.rotationPitch; }
    default boolean getGround() { return mc.player.onGround; }
    default boolean getDead() { return mc.player.isDead; }

}
