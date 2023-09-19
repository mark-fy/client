package wtf.tophat.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public interface Methods {

    Minecraft mc = Minecraft.getMinecraft();

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    default boolean isMoving() { return mc.player.moveForward != 0 || mc.player.moveStrafing != 0; }

    default void sendPacket(Packet<? extends INetHandler> packet) { mc.player.sendQueue.send(packet); }

    default void click(boolean doubleClick) {
        mc.click();
        if(doubleClick)
            mc.click();
    }

}