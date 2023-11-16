package tophat.fun.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C03PacketPlayer;
import tophat.fun.Client;
import tophat.fun.events.Event;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.StringSetting;
import tophat.fun.utilities.player.MoveUtil;

@ModuleInfo(name = "NoFall", desc = "disables fall damage.", category = Module.Category.PLAYER)
public class NoFall extends Module {

    private final StringSetting mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Packet", "Invalid");

    public NoFall() {
        Client.INSTANCE.settingManager.add(
                mode
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            if (mc.thePlayer.fallDistance > 3.0 && MoveUtil.isBlockUnder()) {
                switch (mode.get()) {
                    case "Invalid":
                        mc.thePlayer.onGround = true;
                        mc.thePlayer.motionY = -9999;
                        break;
                    case "Vanilla":
                        mc.thePlayer.onGround = true;
                        break;
                    case "Packet":
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                        break;
                }
                mc.thePlayer.fallDistance = 0;
            }
        }
    }

}
