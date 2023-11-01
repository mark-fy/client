package wtf.tophat.utilities.misc;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * @credit Alan34
 * thx for helping for the raycast alan the gamer
 */
public class RayCast {

    public static Entity raycast(Minecraft mc, double minRange, double maxRange, Entity entity) {
        if (entity == null)
            return null;
        Entity player = mc.player;
        Vec3 posVector = entity.getPositionVector().add(new Vec3(0, entity.getEyeHeight(), 0));
        Vec3 newPosVector = mc.player.getPositionVector().add(new Vec3(0, mc.player.getEyeHeight(), 0));
        AxisAlignedBB a = mc.player.getEntityBoundingBox()
                .addCoord(posVector.xCoord - newPosVector.xCoord, posVector.yCoord - newPosVector.yCoord, posVector.zCoord - newPosVector.zCoord)
                .expand(1.0F, 1.0F, 1.0F);
        List entityList = mc.world.getEntitiesWithinAABBExcludingEntity(player, a);
        double minDistance = minRange + 0.5f;
        double maxDistance = maxRange + 0.5f;
        for (int i = 0; i < entityList.size(); ++i) {
            Entity entity2 = (Entity) entityList.get(i);

            if (entity2.canBeCollidedWith()) {
                float collisionBorderSize = entity2.getCollisionBorderSize();
                AxisAlignedBB alignedBB = entity2.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize,
                        collisionBorderSize);
                MovingObjectPosition objectPosition = alignedBB.calculateIntercept(newPosVector, posVector);

                if (alignedBB.isVecInside(newPosVector)) {
                    if (0.0D < minDistance || minDistance == 0.0D) {
                        minDistance = 0.0D;
                    }
                } else if (objectPosition != null) {
                    double distanceTo = newPosVector.distanceTo(objectPosition.hitVec);

                    if (distanceTo < minDistance || minDistance == 0.0D) {
                        minDistance = distanceTo;
                    }
                }
                if (minDistance <= maxDistance) {
                    return entity2;
                }
            }
        }
        return null;
    }

}