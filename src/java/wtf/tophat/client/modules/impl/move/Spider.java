package wtf.tophat.client.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.CollisionBoxesEvent;
import wtf.tophat.client.events.impl.MotionEvent;
import wtf.tophat.client.events.impl.PacketEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.player.movement.MoveUtil;

@ModuleInfo(name = "Spider",desc = "lets you climb walls", category = Module.Category.MOVE)
public class Spider extends Module {

    private final StringSetting mode;

    public Spider() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Collision", "Verus", "Vulcan")
        );
    }

    @Listen
    public void onPacket(PacketEvent packetEvent) {
        if (getPlayer() == null || getWorld() == null)
            return;

        if(canClimbWall()) {
            if (mode.get().equals("Vulcan")) {
                if (packetEvent.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer packet = (C03PacketPlayer) packetEvent.getPacket();

                    if (mc.player.ticksExisted % 3 == 0) {
                        float yaw = MoveUtil.getDirection();
                        double random = (Math.random() * 0.03 + 0.16);

                        packet.setY(packet.getY() - 0.015);

                        float f = yaw * 0.017453292f;
                        packet.setX(packet.getX() + (MathHelper.sin(f) * random));
                        packet.setZ(packet.getZ() - (MathHelper.cos(f) * random));
                    }

                    if (mc.player.ticksExisted % 2 == 0) {
                        packet.setOnGround(true);
                    }
                }
            }
        }
    }

    @Listen
    public void onCollisionBoxes(CollisionBoxesEvent collisionBoxesEvent) {
        if (getPlayer() == null || getWorld() == null)
            return;

        if(canClimbWall()) {
            if (mode.get().equals("Collision")) {
                if (mc.player.motionY > 0) {
                    return;
                }

                BlockPos blockPos = collisionBoxesEvent.getBlockPos();
                collisionBoxesEvent.setBoundingBox(new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, 1, blockPos.getZ() + 1));
            }
        }
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (canClimbWall()) {
            switch (mode.get()) {
                case "Vanilla":
                case "Vulcan":
                    mc.player.jump();
                    break;
                case "Verus":
                    if (mc.player.ticksExisted % 3 == 0) {
                        mc.player.motionY = 0.42f;
                    }
                    break;
            }
        }
    }
}