package tophat.fun.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import tophat.fun.events.Event;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.events.impl.player.RotationEvent;
import tophat.fun.events.impl.render.Render3DEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.modules.settings.impl.NumberSetting;
import tophat.fun.modules.settings.impl.StringSetting;
import tophat.fun.utilities.Methods;
import tophat.fun.utilities.math.MathUtil;
import tophat.fun.utilities.player.RotationUtil;
import tophat.fun.utilities.render.esp.EntityESPUtil;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "Scaffold", desc = "places blocks under you", category = Module.Category.PLAYER)
public class Scaffold extends Module {

    private final BooleanSetting lockAim = new BooleanSetting(this, "LockAim", false);

    public static EntityLivingBase target;
    int cpsdelay = 0;
    long time = System.currentTimeMillis();
    BlockPos bpos = null;

    @Override
    public void onDisable() {
        cpsdelay = 0;
        super.onDisable();
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (event.getState() == Event.State.PRE) {

            if(!(mc.theWorld.getBlockState(mc.thePlayer.getPosition().down(1)).getBlock() instanceof BlockAir)) {
                bpos = mc.thePlayer.getPosition().down(1);
            }


        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {

    }

    @Listen
    public void onRotation(RotationEvent event) {
        if(event.getState() == Event.State.PRE) {
            if(bpos != null){
                if(lockAim.get()) {
                    mc.thePlayer.rotationYaw = RotationUtil.getRotationsBlock(bpos)[0];
                    mc.thePlayer.rotationPitch = RotationUtil.getRotationsBlock(bpos)[1];
                } else {
                    event.setYaw(RotationUtil.getRotationsBlock(bpos)[0]);
                    event.setPitch(RotationUtil.getRotationsBlock(bpos)[1]);
                }

                mc.thePlayer.rotationYawHead = event.getYaw();
                mc.thePlayer.renderYawOffset = event.getYaw();
                if(mc.objectMouseOver.getBlockPos() == bpos){
                    mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement());
                }

            }
        }
    }

}
