package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.CollisionBoxesEvent;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.module.impl.misc.Blink;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.movement.MoveUtil;

@ModuleInfo(name = "Flight",desc = "fly like a bird", category = Module.Category.MOVE)
public class Flight extends Module {

    private final StringSetting mode;
    private final NumberSetting speed;

    public Flight() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Collision", "Verus", "Intave"),
                speed = new NumberSetting(this, "Speed", 0, 4, 1, 2)
                        .setHidden(() -> !mode.compare("Vanilla"))
        );
    }

    // Verus
    private boolean up;

    @Listen
    public void onMotion(MotionEvent event) {
        switch ((mode.getValue())){
            case "Verus":
                if(event.state == Event.State.PRE) {
                    if (!mc.settings.keyBindJump.isKeyDown()) {
                        if (mc.player.onGround) {
                            mc.player.motionY = 0.42f;
                            up = true;
                        } else if (up) {
                            if (!mc.player.isCollidedHorizontally) {
                                mc.player.motionY = -0.0784000015258789;
                            }
                            up = false;
                        }
                    } else if (mc.player.ticksExisted % 3 == 0) {
                        mc.player.motionY = 0.42f;
                    }
                    MoveUtil.setSpeed(mc.settings.keyBindJump.isKeyDown() ? 0 : 0.33);
                }
                break;
            case "Vanilla":
                mc.player.motionY = 0;

                if (Keyboard.isKeyDown(mc.settings.keyBindJump.getKeyCode())) {
                    mc.player.motionY = speed.getValue().floatValue();
                }

                if (Keyboard.isKeyDown(mc.settings.keyBindSneak.getKeyCode())) {
                    mc.player.motionY = -speed.getValue().floatValue();
                }

                MoveUtil.setSpeed(speed.getValue().floatValue());
                break;
            case "Intave":
                mc.player.motionY = 0.0D;
                MoveUtil.setSpeed(0.2f);
                break;
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if(Methods.mc.player == null || Methods.mc.world == null)
            return;

        switch (mode.getValue()) {
            case "Intave":
                if(event.getPacket() instanceof C0APacketAnimation) {
                    event.setCancelled(true);
                }

                if(event.getPacket() instanceof C19PacketResourcePackStatus) {
                    event.setCancelled(true);
                }

                if(event.getPacket() instanceof C14PacketTabComplete) {
                    event.setCancelled(true);
                }
                break;
        }
    }

    @Listen
    public void onCollision(CollisionBoxesEvent event) {
        if(Methods.mc.player == null || Methods.mc.world == null)
            return;

        switch (mode.getValue()) {
            case "Verus":
                event.setBoundingBox(new AxisAlignedBB(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ()));
                break;
            case "Collision":
                if(!mc.settings.keyBindSneak.pressed)
                    event.setBoundingBox(new AxisAlignedBB(-2, -1, -2, 2, 1, 2).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ()));
                break;
        }
    }

    @Override
    public void onDisable() {
        up = false;
        super.onDisable();
    }
}
