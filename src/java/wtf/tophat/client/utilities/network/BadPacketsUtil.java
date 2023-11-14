package wtf.tophat.client.utilities.network;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import wtf.tophat.client.events.impl.network.PacketEvent;
import wtf.tophat.client.utilities.Methods;

import static net.minecraft.network.play.client.C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT;

/**
 * @author Alan Gaming 69
 */
public class BadPacketsUtil implements Methods {
    private static boolean slot, attack, swing, block, inventory;

    public static boolean bad() {
        return bad(true, true, true, true, true);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if(event.getType() == PacketEvent.Type.OUTGOING){
            final Packet<?> packet = event.getPacket();

            if (packet instanceof C09PacketHeldItemChange) {
                slot = true;
            } else if (packet instanceof C0APacketAnimation) {
                swing = true;
            } else if (packet instanceof C02PacketUseEntity) {
                attack = true;
            } else if (packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C07PacketPlayerDigging) {
                block = true;
            } else if (packet instanceof C0EPacketClickWindow ||
                    (packet instanceof C16PacketClientStatus && ((C16PacketClientStatus) packet).getStatus() == OPEN_INVENTORY_ACHIEVEMENT) ||
                    packet instanceof C0DPacketCloseWindow) {
                inventory = true;
            } else if (packet instanceof C03PacketPlayer) {
                reset();
            }
        }
    };

    public static boolean bad(final boolean slot, final boolean attack, final boolean swing, final boolean block, final boolean inventory) {
        return (BadPacketsUtil.slot && slot) ||
                (BadPacketsUtil.attack && attack) ||
                (BadPacketsUtil.swing && swing) ||
                (BadPacketsUtil.block && block) ||
                (BadPacketsUtil.inventory && inventory);
    }

    public static void reset() {
        slot = false;
        swing = false;
        attack = false;
        block = false;
        inventory = false;
    }
}