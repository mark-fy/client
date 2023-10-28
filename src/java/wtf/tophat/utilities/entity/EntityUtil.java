package wtf.tophat.utilities.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import wtf.tophat.utilities.Methods;

public class EntityUtil implements Methods {

    public static EntityLivingBase getClosestEntity(double range) {
        double dist = range;
        EntityLivingBase target = null;
        for (Entity entity : Minecraft.getMinecraft().world.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) entity;
                if (canAttack(player)) {
                    double currentDist = mc.player.getDistanceToEntity(player);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target = player;
                    }
                }
            }
        }
        return target;
    }

    public static boolean canAttack(Entity entity) {
        if ((!entity.isInvisible())) {
            return entity != mc.player && entity.isEntityAlive() && mc.player != null && Minecraft.getMinecraft().world != null && mc.player.ticksExisted > 30 && entity.ticksExisted > 15;
        } else {
            return false;
        }
    }

}
