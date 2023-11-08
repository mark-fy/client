package wtf.tophat.client.utilities.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;

public class PathUtil {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static List<Vec3> findBlinkPath(double tpX, double tpY, double tpZ) {
        return PathUtil.findBlinkPath(tpX, tpY, tpZ, 5.0);
    }

    public static List<Vec3> findBlinkPath(double tpX, double tpY, double tpZ, double dist) {
        return PathUtil.findBlinkPath(PathUtil.mc.player.posX, PathUtil.mc.player.posY, PathUtil.mc.player.posZ, tpX, tpY, tpZ, dist);
    }

    public static List<Vec3> findBlinkPath(double curX, double curY, double curZ, double tpX, double tpY, double tpZ, double dashDistance) {
        Vec3 topFrom = new Vec3(curX, curY, curZ);
        Vec3 to = new Vec3(tpX, tpY, tpZ);
        if (!PathUtil.canPassThrow(new BlockPos(topFrom))) {
            topFrom = topFrom.addVector(0.0, 1.0, 0.0);
        }
        CustomPathFinder pathfinder = new CustomPathFinder(topFrom, to);
        pathfinder.compute();
        int i2 = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        ArrayList<Vec3> path = new ArrayList<Vec3>();
        ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
        for (Vec3 pathElm : pathFinderPath) {
            if (i2 == 0 || i2 == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0.0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.xCoord, pathElm.xCoord);
                    double smallY = Math.min(lastDashLoc.yCoord, pathElm.yCoord);
                    double smallZ = Math.min(lastDashLoc.zCoord, pathElm.zCoord);
                    double bigX = Math.max(lastDashLoc.xCoord, pathElm.xCoord);
                    double bigY = Math.max(lastDashLoc.yCoord, pathElm.yCoord);
                    double bigZ = Math.max(lastDashLoc.zCoord, pathElm.zCoord);
                    int x2 = (int)smallX;
                    block1: while ((double)x2 <= bigX) {
                        int y2 = (int)smallY;
                        while ((double)y2 <= bigY) {
                            int z2 = (int)smallZ;
                            while ((double)z2 <= bigZ) {
                                if (!CustomPathFinder.checkPositionValidity(x2, y2, z2, false)) {
                                    canContinue = false;
                                    break block1;
                                }
                                ++z2;
                            }
                            ++y2;
                        }
                        ++x2;
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            ++i2;
        }
        return path;
    }

    private static boolean canPassThrow(BlockPos pos) {
        Block block = Minecraft.getMinecraft().world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }

    public static List<Vector3d> findPath(double tpX, double tpY, double tpZ, double offset) {
        ArrayList<Vector3d> positions = new ArrayList<Vector3d>();
        double steps = Math.ceil(PathUtil.getDistance(PathUtil.mc.player.posX, PathUtil.mc.player.posY, PathUtil.mc.player.posZ, tpX, tpY, tpZ) / offset);
        double dX = tpX - PathUtil.mc.player.posX;
        double dY = tpY - PathUtil.mc.player.posY;
        double dZ = tpZ - PathUtil.mc.player.posZ;
        for (double d2 = 1.0; d2 <= steps; d2 += 1.0) {
            positions.add(new Vector3d(PathUtil.mc.player.posX + dX * d2 / steps, PathUtil.mc.player.posY + dY * d2 / steps, PathUtil.mc.player.posZ + dZ * d2 / steps));
        }
        return positions;
    }

    private static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double xDiff = x1 - x2;
        double yDiff = y1 - y2;
        double zDiff = z1 - z2;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
    }
}