package wtf.tophat.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.StringSetting;

@ModuleInfo(name = "No Rotate", desc = "prevents servers from rotating you", category = Module.Category.MISC)
public final class NoRotate extends Module {

    private final StringSetting mode;

    public NoRotate() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Normal", "Normal", "Cancel")
        );
    }

    @Listen
    public void onPacket(PacketEvent e) {
        if (getPlayer() == null || getWorld() == null)
            return;

        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            switch (mode.get()) {
                case "Normal":
                    packet.setYaw(mc.player.rotationYaw);
                    packet.setPitch(mc.player.rotationPitch);
                    break;
                case "Cancel":
                    e.setCancelled(true);
                    break;
            }
        }
    }
}