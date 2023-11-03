package wtf.tophat.utilities.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import wtf.tophat.TopHat;
import wtf.tophat.modules.impl.combat.AntiBot;
import wtf.tophat.utilities.Methods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

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

    public static ArrayList<EntityLivingBase> getEntitiesWithAntiBot_old() {
        ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
        Collection<NetworkPlayerInfo> playerlist = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
        ArrayList<String> playerlists = new ArrayList<String>();
        for (NetworkPlayerInfo info : playerlist) {
            if (info == null || info.getGameProfile() == null || info.getGameProfile().getName() == null) continue;
            playerlists.add(info.getGameProfile().getName());
        }
        for (Entity o2 : mc.world.loadedEntityList) {
            if (o2 instanceof EntityLivingBase) {
                for (String name : playerlists) {
                    if (!name.equals(((EntityLivingBase)((Object)o2)).getName())) continue;
                    Entity e2 = (Entity)((Object)o2);
                    EntityLivingBase elb = (EntityLivingBase)e2;
                    if (elb == mc.player) break;
                    targets.add(elb);
                    break;
                }
            }
            if (!(o2 instanceof EntityZombie)) continue;
            Entity e3 = (Entity)((Object)o2);
            EntityLivingBase elb = (EntityLivingBase)e3;
            targets.add(elb);
            break;
        }
        return targets;
    }

    @Deprecated
    public static ArrayList<EntityLivingBase> getEntities_old(boolean antibot) {
        if (antibot) {
            ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
            Collection<NetworkPlayerInfo> playerlist = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
            ArrayList<String> playerlists = new ArrayList<String>();
            for (NetworkPlayerInfo info : playerlist) {
                if (info == null || info.getGameProfile() == null || info.getGameProfile().getName() == null) continue;
                playerlists.add(info.getGameProfile().getName());
            }
            for (Entity o2 : mc.world.loadedEntityList) {
                EntityLivingBase elb;
                Object e2;
                if (o2 instanceof EntityLivingBase) {
                    for (String name : playerlists) {
                        if (!name.equals(((EntityLivingBase)((Object)o2)).getName()) && TopHat.moduleManager.getByClass(AntiBot.class).isEnabled()) continue;
                        Entity e3 = (Entity)((Object)o2);
                        EntityLivingBase elb2 = (EntityLivingBase)e3;
                        if (elb2 == mc.player || elb2.isOnSameTeam(mc.player)) break;
                        if (elb2.getName().equals("SHOP") || elb2.getName().equals("SHOPS") || elb2.getName().equals("UPGRADE") || elb2.getName().equals("UPGRADES")) continue;
                        targets.add(elb2);
                        break;
                    }
                }
                if (!(o2 instanceof EntityZombie)) continue;
                e2 = (Entity)((Object)o2);
                elb = (EntityLivingBase)e2;
                targets.add(elb);
                break;
            }
            return targets;
        }
        ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
        for (Object o3 : mc.world.loadedEntityList) {
            if (!(o3 instanceof EntityLivingBase)) continue;
            targets.add((EntityLivingBase)o3);
        }
        return targets;
    }

    @Deprecated
    public static EntityLivingBase getClosest_old(List<EntityLivingBase> entities, double range) {
        EntityLivingBase target = null;
        double distance = -1.0;
        for (EntityLivingBase object : entities) {
            EntityLivingBase player;
            Entity entity = object;
            if (!(entity instanceof EntityLivingBase) || (player = (EntityLivingBase)entity) instanceof EntityArmorStand || player instanceof EntitySlime || player == mc.player) continue;
            EntityPlayerSP thePlayer = mc.player;
            AxisAlignedBB entityB = player.getEntityBoundingBox();
            AxisAlignedBB entityBoundingBox = new AxisAlignedBB(entityB.minX, player.posY, entityB.minZ, entityB.maxX, player.posY + (double)player.height, entityB.maxZ);
            double currentDist = entityBoundingBox.getDistanceTo(new Vec3(thePlayer.posX, thePlayer.posY + (double)thePlayer.getEyeHeight(), thePlayer.posZ));
            double deltaX = thePlayer.posX - player.posX;
            double deltaY = thePlayer.posY - player.posY;
            double deltaZ = thePlayer.posZ - player.posZ;
            if (currentDist > range || Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) > range || !((currentDist = (double)player.getHealth()) <= distance) && distance != -1.0) continue;
            distance = currentDist;
            target = player;
        }
        return target;
    }

    public static class RangeSorter
            implements Comparator<Entity> {
        private final double centerX;
        private final double centerY;
        private final double centerZ;

        public RangeSorter(double centerX, double centerY, double centerZ) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
        }

        public RangeSorter() {
            this(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);
        }

        @Override
        public int compare(Entity o1, Entity o2) {
            double x1 = mc.player.posX - o1.posX;
            double y1 = mc.player.posY - o1.posY;
            double z1 = mc.player.posZ - o1.posZ;
            double d1 = Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
            double x2 = mc.player.posX - o2.posX;
            double y2 = mc.player.posY - o2.posY;
            double z2 = mc.player.posZ - o2.posZ;
            double d2 = Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2);
            return Double.compare(d1, d2);
        }
    }

    public static class RangeFilter
            implements Predicate<Entity> {
        private final double range;
        private final double centerX;
        private final double centerY;
        private final double centerZ;

        public RangeFilter(double range, double centerX, double centerY, double centerZ) {
            this.range = range;
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
        }

        public RangeFilter(double range) {
            this(range, mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);
        }

        @Override
        public boolean test(Entity entity) {
            if (entity == null || entity.getCollisionBoundingBox() == null) {
                return false;
            }
            double deltaX = mc.player.posX - entity.posX;
            double deltaY = mc.player.posY - entity.posY;
            double deltaZ = mc.player.posZ - entity.posZ;
            return entity.getCollisionBoundingBox().getDistanceTo(new Vec3(this.centerX, this.centerY, this.centerZ)) < this.range;
        }
    }

    public static class FriendFilter
            implements Predicate<Entity> {
        @Override
        public boolean test(Entity entity) {
            return !(entity instanceof EntityPlayer);
        }
    }

    public static class WhitelistTypeFilter
            implements Predicate<Entity> {
        private List<Class<? extends Entity>> types = new ArrayList<Class<? extends Entity>>();

        public WhitelistTypeFilter(Class<? extends Entity>... allowedTypes) {
            this.types = Arrays.asList(allowedTypes);
        }

        @Override
        public boolean test(Entity entity) {
            for (Class<? extends Entity> type : this.types) {
                if (!type.isInstance(type)) continue;
                return true;
            }
            return false;
        }
    }

    public static class LivingFilter
            implements Predicate<Entity> {
        @Override
        public boolean test(Entity entity) {
            return !(entity instanceof EntityLivingBase) || ((EntityLivingBase)entity).getHealth() > 0.0f;
        }
    }

}
