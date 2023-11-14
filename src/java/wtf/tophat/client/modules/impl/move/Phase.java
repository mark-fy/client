package wtf.tophat.client.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.events.impl.network.PacketEvent;
import wtf.tophat.client.events.impl.world.CollisionBoxesEvent;
import wtf.tophat.client.events.impl.world.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.misc.BlinkUtil;

@ModuleInfo(name = "Phase", desc = "pass trough blocks", category = Module.Category.MOVE)
public class Phase extends Module {

    public final StringSetting mode;

    public Phase(){
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Skip", "Skip", "Full", "Clip")
        );
    }

    int delay;
    boolean canSpeed;

    @Listen
    public void onUpdate(UpdateEvent event) {
        switch (mode.get()){
            case "Skip":
                if (isInsideBlock() && mc.player.isSneaking()) {
                    final float yaw = mc.player.rotationYaw;
                    final float distance = 1.0f;
                    mc.player.getEntityBoundingBox().offsetAndUpdate(distance * Math.cos(Math.toRadians(yaw + 90.0f)), 0.0, distance * Math.sin(Math.toRadians(yaw + 90.0f)));
                }
                break;
            case "Full":
                double multiplier = 0.4;
                final double mx = Math.cos(Math.toRadians(mc.player.rotationYaw + 90.0f));
                final double mz = Math.sin(Math.toRadians(mc.player.rotationYaw + 90.0f));
                final double x = mc.player.movementInput.moveForward * multiplier * mx + mc.player.movementInput.moveStrafe * multiplier * mz;
                final double z = mc.player.movementInput.moveForward * multiplier * mz - mc.player.movementInput.moveStrafe * multiplier * mx;
                if (mc.player.isCollidedHorizontally && !mc.player.isOnLadder() && !isInsideBlock()) {
                    mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX + x, mc.player.posY, mc.player.posZ + z, false));
                    for (int i = 1; i < 11; ++i) {
                        mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, Double.MAX_VALUE * i, mc.player.posZ, false));
                    }
                    final double posX = mc.player.posX;
                    final double posY = mc.player.posY;
                    mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY - (isOnLiquid() ? 9000.0 : 0.1), mc.player.posZ, false));
                    mc.player.setPosition(mc.player.posX + x, mc.player.posY, mc.player.posZ + z);
                }else if(isInsideBlock()){
                    mc.player.setPosition(mc.player.posX + x, mc.player.posY, mc.player.posZ + z);
                }
                break;
            case "Clip":
                mc.player.setPosition(mc.player.posX,
                        mc.player.posY - 2, mc.player.posZ);
                break;
        }

    }

    @Listen
    public void onCollisionBoxes(CollisionBoxesEvent event) {
        if (mc.player == null || mc.world == null)
            return;

        switch (mode.get()) {
            case "Skip":
                if (event.getBoundingBox() != null && event.getBoundingBox().maxY > mc.player.getEntityBoundingBox().minY && mc.player.isSneaking()) {
                    event.setBoundingBox(null);
                }
                break;
            case "Full":
                if(event.getBoundingBox() != null && event.getBoundingBox().maxY > mc.player.boundingBox.minY) {
                    event.setCancelled(true);
                }
                break;
        }
    }

    @Listen
    public void onPacket(PacketEvent event){
        if (event.getType().equals(PacketEvent.Type.OUTGOING)){
            switch (mode.get()){
                case "Full":
                    if (isInsideBlock()) {
                        return;
                    }
                    final double multiplier = 0.2;
                    final double mx = Math.cos(Math.toRadians(mc.player.rotationYaw + 90.0f));
                    final double mz = Math.sin(Math.toRadians(mc.player.rotationYaw + 90.0f));
                    final double x = mc.player.movementInput.moveForward * multiplier * mx + mc.player.movementInput.moveStrafe * multiplier * mz;
                    final double z = mc.player.movementInput.moveForward * multiplier * mz - mc.player.movementInput.moveStrafe * multiplier * mx;
                    Packet packet = event.getPacket();
                    if (mc.player.isCollidedHorizontally && packet instanceof C03PacketPlayer) {
                        delay++;
                        final C03PacketPlayer player = (C03PacketPlayer) packet;
                        if (this.delay >= 5) {
                            player.setX(x + player.getX());
                            player.setY(player.getY() - 1);
                            player.setZ(z + player.getZ());
                            this.delay = 0;
                        }
                    }
                    break;
            }
        }
        if (event.getType().equals(PacketEvent.Type.INCOMING)){
            Packet packet = event.getPacket();
            switch (mode.get()){
                case "Full":
                    if(packet instanceof S08PacketPlayerPosLook) {
                        S08PacketPlayerPosLook s08packet = (S08PacketPlayerPosLook)packet;
                        s08packet.setYaw(mc.player.rotationYaw);
                        s08packet.setPitch(mc.player.rotationPitch);
                        canSpeed = true;
                    }
                    break;
            }
        }
    }

    public static boolean isInsideBlock() {
        for (int x = MathHelper.floor_double(mc.player.getEntityBoundingBox().minX); x < MathHelper
                .floor_double(mc.player.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(mc.player.getEntityBoundingBox().minY); y < MathHelper
                    .floor_double(mc.player.getEntityBoundingBox().maxY) + 1; ++y) {
                for (int z = MathHelper
                        .floor_double(mc.player.getEntityBoundingBox().minZ); z < MathHelper
                        .floor_double(mc.player.getEntityBoundingBox().maxZ) + 1; ++z) {
                    final Block block = mc.world.getBlockState(new BlockPos(x, y, z))
                            .getBlock();
                    final AxisAlignedBB boundingBox;
                    if (block != null && !(block instanceof BlockAir)
                            && (boundingBox = block.getCollisionBoundingBox(mc.world,
                            new BlockPos(x, y, z),
                            mc.world.getBlockState(new BlockPos(x, y, z)))) != null
                            && mc.player.getEntityBoundingBox().intersectsWith(boundingBox)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isOnLiquid() {
        AxisAlignedBB boundingBox = mc.player.getEntityBoundingBox();
        if (boundingBox == null) {
            return false;
        }
        boundingBox = boundingBox.contract(0.01D, 0.0D, 0.01D).offset(0.0D, -0.01D, 0.0D);
        boolean onLiquid = false;
        int y = (int) boundingBox.minY;
        for (int x = MathHelper.floor_double(boundingBox.minX); x < MathHelper
                .floor_double(boundingBox.maxX + 1.0D); x++) {
            for (int z = MathHelper.floor_double(boundingBox.minZ); z < MathHelper
                    .floor_double(boundingBox.maxZ + 1.0D); z++) {
                Block block = mc.world.getBlockState((new BlockPos(x, y, z))).getBlock();
                if (block != Blocks.air) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

}
