package tophat.fun.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import tophat.fun.events.impl.network.PacketEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.NumberSetting;
import tophat.fun.modules.base.settings.impl.StringSetting;

@ModuleInfo(name = "Velocity", desc = "reduces your knockback.", category = Module.Category.COMBAT)
public class Velocity extends Module {

    private final StringSetting mode = new StringSetting(this, "Mode", "Simple", "Simple", "Reverse", "Legit");
    private final NumberSetting horizontal = new NumberSetting(this, "Horizontal", 0, 100, 100, 0).setHidden(() -> !mode.is("Simple") && !mode.is("Reverse"));
    private final NumberSetting vertical = new NumberSetting(this, "Vertical", 0, 100, 100, 0).setHidden(() -> !mode.is("Simple") && !mode.is("Reverse"));

    @Listen
    public void onPacket(PacketEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        switch (mode.get()) {
            case "Simple":
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();

                    if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
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

                    if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                        if (horizontal.get().doubleValue() == 0 && vertical.get().doubleValue() == 0)
                            event.setCancelled(true);

                        packet.setMotionX((int) (packet.getMotionX() * (-horizontal.get().doubleValue() / 100D)));
                        packet.setMotionY((int) (packet.getMotionY() * (-vertical.get().doubleValue() / 100D)));
                        packet.setMotionZ((int) (packet.getMotionZ() * (-horizontal.get().doubleValue() / 100D)));
                    }
                }
                break;

            case "Legit":
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) event.getPacket();

                    if (mc.thePlayer != null && s12.getEntityID() == mc.thePlayer.getEntityId()) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), mc.thePlayer.hurtTime == 9);
                    }
                }
                break;
        }
    }
}
