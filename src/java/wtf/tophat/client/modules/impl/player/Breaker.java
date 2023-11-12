package wtf.tophat.client.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.impl.combat.RotationEvent;
import wtf.tophat.client.events.impl.world.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.modules.impl.combat.KillAura;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.network.PacketUtil;
import wtf.tophat.client.utilities.player.rotations.AimUtil;
import wtf.tophat.client.utilities.player.rotations.FixedRotations;
import wtf.tophat.client.utilities.player.rotations.Rotation;
import wtf.tophat.client.utilities.player.rotations.RotationUtil;

@ModuleInfo(name = "Breaker", desc = "break bed through blocks", category = Module.Category.PLAYER)
public class Breaker extends Module {

    @Listen
    public void onRots(RotationEvent event) {
        if (KillAura.target != null) return;

        if (!event.getState().equals(Event.State.PRE)) {
            return;
        }

        for (int radius = 7, x = -radius; x < radius; ++x) {
            for (int y = radius; y > -radius; --y) {
                for (int z = -radius; z < radius; ++z) {
                    final int xPos = (int) mc.player.posX + x;
                    final int yPos = (int) mc.player.posY + y;
                    final int zPos = (int) mc.player.posZ + z;
                    final BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                    final Block block = mc.world.getBlockState(blockPos).getBlock();
                    if ((block.getBlockState().getBlock() == Block.getBlockById(92) || block.getBlockState().getBlock() == Blocks.bed)) {
                        if (mc.player.swingProgress == 0f) {
                            Rotation rot = AimUtil.attemptFacePosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            event.setYaw(rot.getRotationYaw());
                            event.setPitch(rot.getRotationPitch());
                            mc.player.swingItem();
                            PacketUtil.sendNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                            PacketUtil.sendNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                        }
                    }
                }
            }
        }
    }
}