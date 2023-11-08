package wtf.tophat.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.DividerSetting;
import wtf.tophat.settings.impl.StringSetting;

import java.util.ArrayList;

@ModuleInfo(name = "Disabler",desc = "disables anti cheats", category = Module.Category.MISC)
public class Disabler extends Module {

    private final DividerSetting modes, spacer;
    private final StringSetting mode;
    private final BooleanSetting verusCombat, c00, c13, c0f, c0c, c0b, c03;

    public Disabler() {
        TopHat.settingManager.add(
                modes = new DividerSetting(this, "Mode Settings"),
                mode = new StringSetting(this, "Mode", "Custom", "Custom", "Intave Timer", "Intave 13", "Verus", "Vulcan", "NCP Timer"),
                spacer = new DividerSetting(this, "")
                        .setHidden(() -> mode.is("Intave Timer") || mode.is("NCP Timer") || mode.is("Intave 13")),
                verusCombat = new BooleanSetting(this, "Verus Combat", false)
                        .setHidden(() -> !mode.is("Verus")),
                c00 = new BooleanSetting(this, "C00KeepAlive", false)
                        .setHidden(() -> !mode.is("Custom")),
                c13 = new BooleanSetting(this, "C13PlayerAbilities", false)
                        .setHidden(() -> !mode.is("Custom")),
                c0f = new BooleanSetting(this, "C0FConfirmTransaction", false)
                        .setHidden(() -> !mode.is("Custom")),
                c0c = new BooleanSetting(this, "C0CInput", false)
                        .setHidden(() -> !mode.is("Custom")),
                c0b = new BooleanSetting(this, "C0BEntityAction", false)
                        .setHidden(() -> !mode.is("Custom")),
                c03 = new BooleanSetting(this, "C03PacketPlayer", false)
                        .setHidden(() -> !mode.is("Custom"))
        );
    }

    // Verus Combat
    private int verusCounter;

    private final ArrayList<Packet<?>> transactions = new ArrayList<>();
    int currentTransaction = 0;

    @Listen
    public void onPacket(PacketEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        Packet<?> packet = event.getPacket();

        switch(mode.get()) {
            case "Verus":
                if(verusCombat.get()) {
                    if (packet instanceof C0FPacketConfirmTransaction) {
                        if (getDead()) {
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
            case "Vulcan":
                if(packet instanceof C03PacketPlayer){
                    mc.player.sendQueue.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ), EnumFacing.UP));
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
                if(c03.get() && packet instanceof C03PacketPlayer) {
                    event.setCancelled(true);
                }
                break;
            case "Intave Timer":
                if(packet instanceof C19PacketResourcePackStatus) {
                    event.setCancelled(true);
                }
                break;
            case "Intave 13":
                if (packet instanceof S3FPacketCustomPayload) {
                    S3FPacketCustomPayload customPayloadPacket = (S3FPacketCustomPayload) packet;
                    if (customPayloadPacket.getChannelName().equals("MC|Brand")) {
                        event.setCancelled(true);
                    }
                }
                break;
        }
    }

    @Listen
    public void onMotion(MotionEvent event) {
        switch (mode.get()) {
            case "Intave Timer":
                StringBuilder builder = new StringBuilder();
                for (int i = 32; i < 256; i++) builder.append((char) i);
                sendPacketUnlogged(new C19PacketResourcePackStatus(builder.toString(), C19PacketResourcePackStatus.Action.ACCEPTED));
                sendPacketUnlogged(new C19PacketResourcePackStatus(builder.toString(), C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                break;
            case "NCP Timer":
                if (mc.player.ticksExisted % 30 == 0) {
                    sendPacketUnlogged(new C03PacketPlayer.C06PacketPlayerPosLook(getX(), getY() - (getGround() ? 0.1D : 1.1D), getZ(), getYaw(), getPitch(), getGround()));
                }
                break;
        }
    }

    @Override
    public void onEnable() {
        verusCounter = 0;
        getPlayer().ticksExisted = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        verusCounter = 0;
        transactions.clear();
        currentTransaction = 0;
        super.onDisable();
    }
}
