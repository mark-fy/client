package wtf.tophat.client.events.impl;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import wtf.tophat.client.events.base.Event;

public class CollisionBoxesEvent extends Event {

    private final BlockPos blockPos;
    private final Block block;
    private AxisAlignedBB boundingBox;
    private final World world;

    public CollisionBoxesEvent(World world, Block block, BlockPos blockPos, AxisAlignedBB boundingBox) {
        this.world = world;
        this.block = block;
        this.blockPos = blockPos;
        this.boundingBox = boundingBox;
    }

    public World getWorld() {
        return world;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getBlockPos() { return blockPos; }

    public AxisAlignedBB getBoundingBox() { return boundingBox; }

    public void setBoundingBox(AxisAlignedBB boundingBox) { this.boundingBox = boundingBox; }
}
