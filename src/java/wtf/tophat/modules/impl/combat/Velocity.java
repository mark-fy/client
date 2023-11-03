package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.move.Speed;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.settings.impl.NumberSetting;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleInfo(name = "Velocity",desc = "disables knockback", category = Module.Category.COMBAT)
public class Velocity extends Module {

    private final StringSetting mode;
    private final NumberSetting horizontal,vertical;

    public Velocity() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Simple", "Simple", "Reverse", "Grim", "Matrix", "C0F Cancel", "Legit", "Cubecraft"),
                horizontal = new NumberSetting(this, "Horizontal", 0, 100, 100, 0)
                        .setHidden(() -> !mode.is("Simple") || !mode.is("Reverse")),
                vertical = new NumberSetting(this, "Vertical", 0, 100, 100, 0)
                        .setHidden(() -> !mode.is("Simple") || !mode.is("Reverse"))
        );
    }

    // Grim
    private final Queue<Short> transactionQueue = new ConcurrentLinkedQueue<>();
    private boolean grimPacket;

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mode.get().equals("Grim")) {
            if (transactionQueue.isEmpty() && grimPacket) {
                grimPacket = false;
            }
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        switch (mode.get()) {
            case "Simple":
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                    if (packet.getEntityID() == mc.player.getEntityId()) {
                        if (horizontal.get().doubleValue() == 0 && vertical.get().doubleValue() == 0)
                            event.setCancelled(true);
                        packet.setMotionX((int) (packet.getMotionX() * (horizontal.get().doubleValue() / 100D)));
                        packet.setMotionY((int) (packet.getMotionY() * (vertical.get().doubleValue() / 100D)));
                        packet.setMotionZ((int) (packet.getMotionZ() * (horizontal.get().doubleValue() / 100D)));
                    }
                }
                break;
            case "Reverse":
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                    if (packet.getEntityID() == mc.player.getEntityId()) {
                        if (horizontal.get().doubleValue() == 0 && vertical.get().doubleValue() == 0)
                            event.setCancelled(true);
                        packet.setMotionX((int) (packet.getMotionX() * (-horizontal.get().doubleValue() / 100D)));
                        packet.setMotionY((int) (packet.getMotionY() * (-vertical.get().doubleValue() / 100D)));
                        packet.setMotionZ((int) (packet.getMotionZ() * (-horizontal.get().doubleValue() / 100D)));
                    }
                }
                break;
            case "Grim":
                if (event.getType() == PacketEvent.Type.INCOMING) {
                    Packet<?> p = event.getPacket();
                    if (p instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) p).getEntityID() == mc.player.getEntityId()) {
                        event.setCancelled(true);
                        grimPacket = true;
                    } else if (p instanceof S32PacketConfirmTransaction) {
                        if (!grimPacket) return;
                        event.setCancelled(true);
                        transactionQueue.add(((S32PacketConfirmTransaction) p).getActionNumber());
                    }
                } else {
                    if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                        if (!grimPacket || transactionQueue.isEmpty()) return;
                        if (transactionQueue.remove(((C0FPacketConfirmTransaction) event.getPacket()).getUid()))
                            event.setCancelled(true);
                    }
                }
                break;
            case "Matrix":
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) event.getPacket();
                    if (mc.player != null && s12.getEntityID() == mc.player.getEntityId()) {
                        s12.motionX *= 5 / 100.0;
                        s12.motionZ *= 5 / 100.0;
                        s12.motionY *= 100 / 100.0;
                    }
                }
                break;
            case "C0F Cancel":
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) event.getPacket();
                    if (mc.player != null && s12.getEntityID() == mc.player.getEntityId()) {
                        event.setCancelled(true);
                    }
                }
                if (event.getPacket() instanceof S27PacketExplosion) {
                    event.setCancelled(true);
                }
                break;
            case "Legit":
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) event.getPacket();
                    if (mc.player != null && s12.getEntityID() == mc.player.getEntityId()) {
                        if (mc.player.hurtTime == 9) {
                            KeyBinding.setKeyBindState(mc.settings.keyBindJump.getKeyCode(), true);
                        } else {
                            KeyBinding.setKeyBindState(mc.settings.keyBindJump.getKeyCode(), false);
                        }
                    }
                }
                break;
            case "Cubecraft":
                if (mc.player.hurtTime != 0) {
                    if(mc.player.onGround){
                        mc.player.motionY = 0.42F;
                        mc.timer.timerSpeed = 1F;
                        Minecraft.getMinecraft().settings.keyBindJump.pressed = true;
                    } else {
                        boolean boost2 = (Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90.0F);
                        mc.timer.timerSpeed = 1F;
                        double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
                        double speed = boost2 ? 1 : 1D;
                        double direction = Speed.getDirection();
                        mc.player.motionX = -Math.sin(direction) * speed * currentSpeed;
                        mc.player.motionZ = Math.cos(direction) * speed * currentSpeed;

                    }
                }
                break;
        }
    }

    @Override
    public void onDisable() {
        grimPacket = false;
        transactionQueue.clear();
        super.onDisable();
    }
}
