package wtf.tophat.client.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.impl.MotionEvent;
import wtf.tophat.client.events.impl.PacketEvent;
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

    public boolean canFall() {
        return mc.player.isEntityAlive() && mc.world != null && !mc.player.isInWater() && !mc.player.isInLava()
                && PlayerUtil.isBlockUnderNoCollisions();
    }

    private final StringSetting mode;

    public NoFall() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Packet", "Verus", "Vulcan", "Blink")
        );
    }

    private final List<Vec3> vectors = new ArrayList<>();
    private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();
    private boolean shouldBlink;

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            if (mc.player.fallDistance > 3.0 && MoveUtil.isBlockUnder()) {
                switch (mode.get()) {
                    case "Vanilla":
                        event.setOnGround(true);
                        break;
                    case "Packet":
                        sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
                        break;
                    case "Blink":
                        if (canFall() && !mc.isIntegratedServerRunning()) {
                            if (mc.player.fallDistance > 2.5 && !mc.player.isInWater() && !mc.player.capabilities.isFlying) {
                                this.shouldBlink = true;

                                if (this.packets.size() > 0) {
                                    event.setOnGround(true);

                                    double posX = mc.player.posX;
                                    double posY = mc.player.posY;
                                    double posZ = mc.player.posZ;

                                    if (posX != mc.player.lastTickPosX
                                            || posY != mc.player.lastTickPosY
                                            || posZ != mc.player.lastTickPosZ) {
                                        this.vectors.add(new Vec3(posX, posY, posZ));
                                    }
                                }
                            } else {
                                if (this.shouldBlink) {
                                    this.packets.forEach(packet -> {
                                        this.packets.remove(packet);
                                        mc.player.sendQueue.getNetworkManager().sendPacketNoEvent(packet);
                                    });

                                    this.vectors.clear();
                                    this.shouldBlink = false;
                                }
                            }
                        }
                        break;
                }
            }
            mc.player.fallDistance = 0;
        }
    }


    @Override
    public void onEnable(){
        switch (mode.get()){
            case "Blink":
                this.vectors.clear();
                this.packets.clear();
                this.shouldBlink = false;
                break;
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
            case "Blink":
                if (mc.player != null) {
                    if (this.shouldBlink) {
                        if (event.getPacket() instanceof C03PacketPlayer) {
                            event.setCancelled(true);
                            this.packets.add(event.getPacket());
                        }
                    }
                }
                break;
        }
    }
}
