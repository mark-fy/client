package wtf.tophat.module.impl.misc;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import wtf.tophat.Client;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.Methods;

@ModuleInfo(name = "Disabler",desc = "disables anti cheats", category = Module.Category.MISC)
public class Disabler extends Module {

    private final StringSetting mode;
    private final BooleanSetting verusCombat, c00, c13, c0f, c0c, c0b;

    public Disabler() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "Custom", "Intave Timer", "Verus"),
                verusCombat = new BooleanSetting(this, "Verus Combat", false)
                        .setHidden( () -> !mode.compare("Verus") ),
                c00 = new BooleanSetting(this, "C00KeepAlive", false)
                        .setHidden( () -> !mode.compare("Custom") ),
                c13 = new BooleanSetting(this, "C13PlayerAbilities", false)
                        .setHidden( () -> !mode.compare("Custom") ),
                c0f = new BooleanSetting(this, "C0FConfirmTransaction", false)
                        .setHidden( () -> !mode.compare("Custom") ),
                c0c = new BooleanSetting(this, "C0CInput", false)
                        .setHidden( () -> !mode.compare("Custom") ),
                c0b = new BooleanSetting(this, "C0BEntityAction", false)
                        .setHidden( () -> !mode.compare("Custom") )
        );
    }

    // Verus Combat
    private int verusCounter;

    @Listen
    public void onPacket(PacketEvent event) {

        if (Methods.mc.player == null || Methods.mc.world == null)
            return;

        Packet<?> packet = event.getPacket();

        switch(mode.get()) {
            case "Verus":
                if(verusCombat.get()) {
                    if (packet instanceof C0FPacketConfirmTransaction) {
                        if (mc.player.isDead) {
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
                break;
            case "Custom":
                if(c00.get() && packet instanceof C00PacketKeepAlive) {
                    event.setCancelled(true);
                }

                if(c13.get() && packet instanceof C13PacketPlayerAbilities) {
                    event.setCancelled(true);
                }

                if(c0f.get() && packet instanceof C0FPacketConfirmTransaction) {
                    event.setCancelled(true);
                }

                if(c0c.get() && packet instanceof C0CPacketInput) {
                    event.setCancelled(true);
                }

                if(c0b.get() && packet instanceof C0BPacketEntityAction) {
                    event.setCancelled(true);
                }
                break;
            case "Intave Timer":
                if(packet instanceof C19PacketResourcePackStatus) {
                    event.setCancelled(true);
                }
                break;
        }
    }

    @Listen
    public void onMotion(MotionEvent event) {
        switch (mode.get()) {
            case "Intave Timer":
                StringBuilder builder = new StringBuilder();
                for (int i = 32; i < 256; i++) builder.append((char)i);
                sendPacketUnlogged(new C19PacketResourcePackStatus(builder.toString(), C19PacketResourcePackStatus.Action.ACCEPTED));
                sendPacketUnlogged(new C19PacketResourcePackStatus(builder.toString(), C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                break;
        }
    }

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
}
