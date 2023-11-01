package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;

@ModuleInfo(name = "W Tap", desc = "sprint reset", category = Module.Category.COMBAT)
public class WTap extends Module {

    @Listen
    public void onPacket(PacketEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        if(event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();
            if ((packet.getAction() == C02PacketUseEntity.Action.ATTACK) && (packet.getEntityFromWorld(mc.world) != mc.player) && (mc.player.getFoodStats().getFoodLevel() > 6)) {
                boolean sprint = mc.player.isSprinting();
                mc.player.setSprinting(false);
                mc.player.sendQueue.send(new C0BPacketEntityAction(mc.player, C0BPacketEntityAction.Action.STOP_SPRINTING));
                mc.player.sendQueue.send(new C0BPacketEntityAction(mc.player, C0BPacketEntityAction.Action.START_SPRINTING));
                mc.player.setSprinting(sprint);
            }
        }
    }
}
