package wtf.tophat.client.utilities.network;

import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import wtf.tophat.client.utilities.Methods;

public class PacketUtil implements Methods {

    public static void sendBlocking(boolean callEvent, boolean place) {
        C08PacketPlayerBlockPlacement packet = place ?
                new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.player.getHeldItem(), 0, 0, 0) :
                new C08PacketPlayerBlockPlacement(mc.player.getHeldItem());
        if(callEvent) {
            mc.player.sendQueue.send(packet);
        }
    }

}
