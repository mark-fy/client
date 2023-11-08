package wtf.tophat.client.utilities.player;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
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
