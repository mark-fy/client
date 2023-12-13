package tophat.fun.modules.impl.others;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.events.impl.network.PacketEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.BooleanSetting;

@ModuleInfo(name = "Disabler", desc = "disable anticheat checks.", category = Module.Category.OTHERS)
public class Disabler extends Module {

    private final BooleanSetting intaveCPS = new BooleanSetting(this, "Intave CPS", false);
    private final BooleanSetting intave13 = new BooleanSetting(this, "Intave 13", false);
    private final BooleanSetting intave14Timer = new BooleanSetting(this, "Intave 14 Timer", false);
    private final BooleanSetting verusCombat = new BooleanSetting(this, "Verus Combat", false);
    private final BooleanSetting oldVulcanStrafe = new BooleanSetting(this, "Old Vulcan Strafe", false);

    // Verus Combat
    private int verusCounter;

    @Override
    public void onEnable() {
        verusCounter = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        verusCounter = 0;
        super.onDisable();
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if(mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        Packet<?> packet = event.getPacket();

        if(intaveCPS.get()) {
            if(packet instanceof C19PacketResourcePackStatus) {
                event.setCancelled(true);
            }

            if(packet instanceof C0APacketAnimation) {
                event.setCancelled(true);
            }
        }

        if (intave13.get()) {
            if (packet instanceof S3FPacketCustomPayload) {
                S3FPacketCustomPayload customPayloadPacket = (S3FPacketCustomPayload) packet;
                if (customPayloadPacket.getChannelName().equals("MC|Brand")) {
                    event.setCancelled(true);
                }
            }
        }

        if(intave14Timer.get()) {
            if(packet instanceof C19PacketResourcePackStatus) {
                event.setCancelled(true);
            }
        }

        if(oldVulcanStrafe.get()) {
            if(packet instanceof C03PacketPlayer){
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ), EnumFacing.UP));
            }
        }

        if(verusCombat.get()) {
            if (packet instanceof C0FPacketConfirmTransaction) {
                if (mc.thePlayer.isDead) {
                    verusCounter = 0;
                }

                if (verusCounter != 0) {
                    event.setCancelled(true);
                }

                verusCounter++;
            } else if (packet instanceof C0BPacketEntityAction) {
                event.setCancelled(true);
            }
        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if(intave14Timer.get()) {
            StringBuilder builder = new StringBuilder();
            for (int i = 32; i < 256; i++) builder.append((char) i);
            sendPacketUnlogged(new C19PacketResourcePackStatus(builder.toString(), C19PacketResourcePackStatus.Action.ACCEPTED));
            sendPacketUnlogged(new C19PacketResourcePackStatus(builder.toString(), C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
        }
    }

}
