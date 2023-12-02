package tophat.fun.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import tophat.fun.events.Event;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.events.impl.player.RotationEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.utilities.player.RotationUtil;

import java.awt.*;

import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;

@ModuleInfo(name = "Scaffold", desc = "places blocks under you", category = Module.Category.PLAYER)
public class Scaffold extends Module {

    private final BooleanSetting lockAim = new BooleanSetting(this, "LockAim", true);
    private final BooleanSetting lockyaw = new BooleanSetting(this, "LockAimYaw", false);
    long time = System.currentTimeMillis();
    Vec3 bpos = null;
    Vec3 vec = null;
    boolean place = false;

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (event.getState() == Event.State.PRE) {

            vec = mc.thePlayer.getPositionVector().addVector(0.0, -0.5, 0.0);
            if(!(mc.theWorld.getBlockState(BlockPos.ORIGIN.add(vec.xCoord, vec.yCoord, vec.zCoord)).getBlock() instanceof BlockAir)) {
                bpos = vec.addVector(0.0, -0.5, 0.0);
            }else{
                place = true;
            }


        }
    }

    Robot robot;
    {
        try {
            System.setProperty("java.awt.headless", "false");
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @Listen
    public void onRotation(RotationEvent event) {
        if(event.getState() == Event.State.PRE) {
            if(bpos != null && mc.thePlayer.getDistanceSq(new BlockPos(bpos.xCoord, bpos.yCoord, bpos.zCoord)) <= 4.5 * 4.5/* && mc.theWorld.getBlockState(BlockPos.ORIGIN.add(vec.xCoord, vec.yCoord, vec.zCoord)).getBlock() instanceof BlockAir*/){
                if(lockAim.get()) {
                    if(lockyaw.get()) {
                        mc.thePlayer.rotationYaw = RotationUtil.getRotationsBlock(bpos)[0];
                    }
                    mc.thePlayer.rotationPitch = RotationUtil.getRotationsBlock(bpos)[1];
                } else {
                    event.setYaw(RotationUtil.getRotationsBlock(bpos)[0]);
                    event.setPitch(RotationUtil.getRotationsBlock(bpos)[1]);
                }

                mc.thePlayer.rotationYawHead = event.getYaw();
                mc.thePlayer.renderYawOffset = event.getYaw();
                mc.thePlayer.rotationPitchHead = event.getPitch();

                if(place){
                    robot.mousePress(BUTTON3_DOWN_MASK);
                    robot.mouseRelease(BUTTON3_DOWN_MASK);
                    place = false;
                }

            }
        }
    }

}
