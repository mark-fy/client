package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import wtf.tophat.Client;
import wtf.tophat.events.impl.CollisionBoxesEvent;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.movement.MoveUtil;


@ModuleInfo(name = "Spider",desc = "lets you climb walls", category = Module.Category.MOVE)
public class Spider extends Module {

    private final StringSetting mode;

    public Spider() {
        Client.settingManager.add(
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

    private boolean canClimbWall() {
        return mc.player != null && mc.player.isCollidedHorizontally && !mc.player.isOnLadder() && !mc.player.isInWater() && mc.player.fallDistance < 1.0F;
    }
}
