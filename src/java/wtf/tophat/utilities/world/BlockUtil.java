package wtf.tophat.utilities.world;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import wtf.tophat.utilities.Methods;

public class BlockUtil implements Methods {
    public static Material getBlockMaterial(BlockPos blockPos) {
        return mc.world.getBlockState(blockPos).getBlock().getMaterial();
    }

    public static ArrayList<BlockPos> getBlocks(float range) {
        ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
        for (float x2 = -range; x2 < range - 1.0f; x2 += 1.0f) {
            for (float y2 = -range; y2 < range - 1.0f; y2 += 1.0f) {
                for (float z2 = -range; z2 < range - 1.0f; z2 += 1.0f) {
                    BlockPos blockPos = new BlockPos((float)mc.player.getPosition().getX() + x2, (float)mc.player.getPosition().getY() + y2, (float)mc.player.getPosition().getZ() + z2);
                    blocks.add(blockPos);
                }
            }
        }
        return blocks;
    }

    public static ArrayList<BlockPos> searchForBlock(String blockName, float range) {
        ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
        for (float x2 = -range; x2 < range - 1.0f; x2 += 1.0f) {
            for (float y2 = -range; y2 < range - 1.0f; y2 += 1.0f) {
                for (float z2 = -range; z2 < range - 1.0f; z2 += 1.0f) {
                    BlockPos blockPos = new BlockPos((float)mc.player.getPosition().getX() + x2, (float)mc.player.getPosition().getY() + y2, (float)mc.player.getPosition().getZ() + z2);
                    if (!mc.world.getBlockState(blockPos).getBlock().getUnlocalizedName().equals(blockName)) continue;
                    blocks.add(blockPos);
                }
            }
        }
        return blocks;
    }

    public static Vec3 floorVec3(Vec3 input) {
        return new Vec3(Math.floor(input.xCoord), Math.floor(input.yCoord), Math.floor(input.zCoord));
    }

    public static Block getBlock(BlockPos pos) {
        if (Minecraft.getMinecraft().world == null) {
            return null;
        }
        return Minecraft.getMinecraft().world.getBlockState(pos).getBlock();
    }
}