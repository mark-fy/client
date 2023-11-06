package wtf.tophat.utilities.world;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockInfo {

    private BlockPos pos;
    private EnumFacing facing;

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    public BlockInfo(BlockPos pos, EnumFacing facing) {
        this.pos = pos;
        this.facing = facing;
    }
}