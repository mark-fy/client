package tophat.fun.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import tophat.fun.events.Event;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.events.impl.player.RotationEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.BooleanSetting;
import tophat.fun.modules.base.settings.impl.NumberSetting;
import tophat.fun.utilities.player.RotationUtil;

import java.awt.*;

import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;

@ModuleInfo(name = "Scaffold", desc = "places blocks under you, currently moonwalks and can diagonal if you setup and get lucky!", category = Module.Category.PLAYER)
public class Scaffold extends Module {

    private final BooleanSetting lockAim = new BooleanSetting(this, "LockAim", true);
    private final NumberSetting y = new NumberSetting(this, "YDiagonalModifier", -2.0, 2.0, 0, 1);
    long time = System.currentTimeMillis();
    Vec3 bpos = null;
    Vec3 vec = null;
    boolean place = false;
    Vec3 yaw;
    float reqyaw;

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (event.getState() == Event.State.PRE) {

            vec = mc.thePlayer.getPositionVector().addVector(0.0, -0.5, 0.0);
            if(mc.theWorld.getBlockState(BlockPos.ORIGIN.add(vec.xCoord, vec.yCoord, vec.zCoord)).getBlock() instanceof BlockAir) {
                place = true;
            }else{
                bpos = vec.addVector(0.0, -0.5, 0.0);
                BlockPos yawbpos = BlockPos.ORIGIN.add(vec.xCoord, vec.yCoord, vec.zCoord);
                yaw = new Vec3(yawbpos.getX() + 0.5, yawbpos.getY() + 0.5, yawbpos.getZ() + 0.5);
                reqyaw = 45 * (Math.round(RotationUtil.getRotationsToVector(yaw)[0] / 45));
            }

            if(reqyaw % 45 == 0 && reqyaw % 90 != 0){
                bpos = bpos.addVector(0.0, y.value.doubleValue(), 0.0);
                System.out.println(y.value.doubleValue());
            }
        }
    }

    Robot robot;
    {
        try {
            System.setProperty("java.awt.headless", "false");
            robot = new Robot();
        } catch (AWTException ignored) {
        }
    }

    @Listen
    public void onRotation(RotationEvent event) {
        if(event.getState() == Event.State.PRE) {

            if(bpos != null && yaw != null && mc.thePlayer.getDistanceSq(new BlockPos(bpos.xCoord, bpos.yCoord, bpos.zCoord)) <= 4.5 * 4.5 && mc.thePlayer.posY > bpos.yCoord/* && mc.theWorld.getBlockState(BlockPos.ORIGIN.add(vec.xCoord, vec.yCoord, vec.zCoord)).getBlock() instanceof BlockAir*/){

                if(lockAim.get()) {

                    if(mc.theWorld.getBlockState(BlockPos.ORIGIN.add(vec.xCoord, vec.yCoord, vec.zCoord)).getBlock() instanceof BlockAir) {
                        mc.thePlayer.rotationYaw = reqyaw;
                    }
                    mc.thePlayer.rotationPitch = RotationUtil.getRotationsToVector(bpos)[1];

                } else {

                    if(mc.theWorld.getBlockState(BlockPos.ORIGIN.add(vec.xCoord, vec.yCoord, vec.zCoord)).getBlock() instanceof BlockAir) {
                        event.setYaw(RotationUtil.getRotationsToVector(yaw)[0]);
                    }
                    event.setPitch(RotationUtil.getRotationsToVector(bpos)[1]);
                }

                mc.thePlayer.rotationYawHead = event.getYaw();
                mc.thePlayer.renderYawOffset = event.getYaw();
                mc.thePlayer.rotationPitchHead = event.getPitch();

                if(place && mc.currentScreen == null){
                    robot.mousePress(BUTTON3_DOWN_MASK);
                    robot.mouseRelease(BUTTON3_DOWN_MASK);
                    robot.mousePress(BUTTON3_DOWN_MASK);
                    robot.mouseRelease(BUTTON3_DOWN_MASK);
                    place = false;
                }

            }
        }
    }

}
