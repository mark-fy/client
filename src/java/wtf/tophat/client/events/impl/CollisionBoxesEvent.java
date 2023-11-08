package wtf.tophat.client.events.impl;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import wtf.tophat.client.events.base.Event;

public class CollisionBoxesEvent extends Event {

    private final BlockPos blockPos;

    private AxisAlignedBB boundingBox;

    public CollisionBoxesEvent(BlockPos blockPos, AxisAlignedBB boundingBox) {
        this.blockPos = blockPos;
        this.boundingBox = boundingBox;
    }

    public BlockPos getBlockPos() { return blockPos; }

    public AxisAlignedBB getBoundingBox() { return boundingBox; }

    public void setBoundingBox(AxisAlignedBB boundingBox) { this.boundingBox = boundingBox; }

}
