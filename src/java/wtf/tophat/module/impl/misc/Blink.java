package wtf.tophat.module.impl.misc;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import wtf.tophat.Client;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.math.TimeUtil;

import java.util.ArrayDeque;

@ModuleInfo(name = "Blink", desc = "blocks your packets for a time being", category = Module.Category.EXPLOIT)
public class Blink extends Module {
    private final BooleanSetting incoming, pulse;
    private final NumberSetting pulseDelay;

    public Blink() {
        Client.settingManager.add(
                incoming = new BooleanSetting(this, "Incoming", false),
                pulse = new BooleanSetting(this, "Pulse", false),
                pulseDelay = new NumberSetting(this, "Pulse Delay", 50, 5000, 150, 1)
        );
    }

    private final ArrayDeque<Packet<?>> outPacketDeque = new ArrayDeque<>();
    private final TimeUtil fakeLagTimer = new TimeUtil();

    boolean active = false;

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        if(active && !this.isEnabled()) {
            while (!outPacketDeque.isEmpty()) {
                sendPacketUnlogged(outPacketDeque.poll());
            }
            active = false;
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
    	if(getPlayer() == null || getWorld() == null)
    		this.setEnabled(false);

        if (active && (event.getType() == PacketEvent.Type.OUTGOING || incoming.get())) {
            outPacketDeque.add(event.getPacket());
            if (pulse.get() && fakeLagTimer.elapsed(pulseDelay.get().longValue())) {
                while (!outPacketDeque.isEmpty()) {
                    sendPacketUnlogged(outPacketDeque.poll());
                }
                fakeLagTimer.reset();
            }
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {
        active = true;
        super.onEnable();
    }
}