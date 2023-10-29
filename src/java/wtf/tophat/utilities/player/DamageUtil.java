package wtf.tophat.utilities.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.player.movement.MoveUtil;

public class DamageUtil implements Methods {

    public static void damage(DamageType type) {
        if (mc.player == null) return;
        double x = mc.player.posX, y = mc.player.posY, z = mc.player.posZ;
        switch (type) {
            case WATCHDOG:
                for(int i = 0; i < 49; i++) {
                    mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY + 0.0625, mc.player.posZ, false));
                    mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                }
                mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY, mc.player.posZ, true));
                break;
            case VANILLA:
                mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 3.01, z, false));
                mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                mc.player.sendQueue.send(new C03PacketPlayer(true));
                break;
        }
    }

    public enum DamageType {
        WATCHDOG, VANILLA
    }
}