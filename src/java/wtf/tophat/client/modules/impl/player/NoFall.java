package wtf.tophat.client.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.events.impl.network.PacketEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.Methods;
import wtf.tophat.client.utilities.math.vector.Vec3;
import wtf.tophat.client.utilities.player.PlayerUtil;
import wtf.tophat.client.utilities.player.movement.MoveUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleInfo(name = "No Fall",desc = "disables fall damage", category = Module.Category.PLAYER)
public class NoFall extends Module {

    private final StringSetting mode;

    public NoFall() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Packet", "Verus", "Vulcan", "Invalid")
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            if (mc.player.fallDistance > 3.0 && MoveUtil.isBlockUnder()) {
                switch (mode.get()) {
                    case "Invalid":
                        mc.player.onGround = true;
                        mc.player.motionY = -9999;
                        break;
                    case "Vanilla":
                        event.setOnGround(true);
                        break;
                    case "Packet":
                        sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
                        break;
                }
                mc.player.fallDistance = 0;
            }
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if(Methods.mc.player == null || Methods.mc.world == null)
            return;

        float modulo =  mc.player.fallDistance % 3;
        boolean correctModulo = modulo < 1f && mc.player.fallDistance > 3;

        switch(mode.get()) {
            case "Vulcan":
                if(event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();
                    if(correctModulo) {
                        mc.player.motionY = -500;
                        packet.setOnGround(true);
                    } else {
                        mc.timer.timerSpeed = 1f;
                    }
                }
                break;
            case "Verus":
                if(mc.player.fallDistance - mc.player.motionY > 3) {
                    mc.player.motionY = 0.0;
                    mc.player.motionX *= 0.6;
                    mc.player.motionZ *= 0.6;
                    sendPacketUnlogged(new C03PacketPlayer(true));
                }
                break;
        }
    }
}
