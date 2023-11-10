package wtf.tophat.client.utilities.player;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import wtf.tophat.client.utilities.Methods;

public class PlayerUtil implements Methods {

    public static boolean isMathGround() {
        return mc.player.posY % 0.015625 == 0;
    }

    public static boolean isOnSameTeam(EntityLivingBase entity) {
        if (entity.getTeam() != null && mc.player.getTeam() != null) {
            char c1 = entity.getDisplayName().getFormattedText().charAt(1);
            char c2 = mc.player.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        } else {
            return false;
        }
    }

    public static boolean isHoldingSword() {
        return mc.player.ticksExisted > 3 && mc.player.getCurrentEquippedItem() != null && mc.player.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

    public boolean isBlockUnder(final double height) {
        return isBlockUnder(height, true);
    }

    public static boolean isBlockUnder(final double height, final boolean boundingBox) {
        if (boundingBox) {
            for (int offset = 0; offset < height; offset += 2) {
                final AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, -offset, 0);

                if (!mc.world.getCollidingBoundingBoxes(mc.player, bb).isEmpty()) {
                    return true;
                }
            }
        } else {
            for (int offset = 0; offset < height; offset++) {
                if (PlayerUtil.blockRelativeToPlayer(0, -offset, 0).isFullBlock()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isBlockUnder() {
        return isBlockUnder(mc.player.posY + mc.player.getEyeHeight());
    }

    public static Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.world.getBlockState(new BlockPos(mc.player).add(offsetX, offsetY, offsetZ)).getBlock();
    }

    public static boolean isBlockUnderNoCollisions() {
        for (int offset = 0; offset < mc.player.posY + mc.player.getEyeHeight(); offset += 2) {
            BlockPos blockPos = new BlockPos(mc.player.posX, offset, mc.player.posZ);

            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.air) {
                return true;
            }
        }
        return false;
    }

    public Block block(final double x, final double y, final double z) {
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static Block block(final BlockPos blockPos) {
        return mc.world.getBlockState(blockPos).getBlock();
    }

    public static boolean isBlockBlacklisted(Item item) {
        return item instanceof ItemAnvilBlock || item.getUnlocalizedName().contains("sand") || item.getUnlocalizedName().contains("gravel") || item.getUnlocalizedName().contains("ladder") || item.getUnlocalizedName().contains("tnt") || item.getUnlocalizedName().contains("chest") || item.getUnlocalizedName().contains("web");
    }

}
