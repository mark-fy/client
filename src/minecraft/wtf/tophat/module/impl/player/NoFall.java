package wtf.tophat.module.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.tophat.events.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;

@ModuleInfo(name = "NoFall",desc = "disables fall damage", category = Module.Category.PLAYER)
public class NoFall extends Module {

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.state == Event.State.PRE) {
            mc.getNetHandler().send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
        }
    }

}
