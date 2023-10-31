package wtf.tophat.modules.impl.combat;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;
import wtf.tophat.Client;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.entity.EntityUtil;
import wtf.tophat.utilities.misc.PathUtil;

@ModuleInfo(name = "Teleport Aura", desc = "attack entities with infinity range", category = Module.Category.COMBAT)
public class TeleportAura extends Module {

    public EntityLivingBase target;
    public final List<Vec3> path = new ArrayList<>();

    private final NumberSetting range;
    private final BooleanSetting onlyPlayer;

    public TeleportAura() {
        Client.settingManager.add(
                range = new NumberSetting(this, "Range", 5.0, 100.0, 50.0, 1),
                onlyPlayer = new BooleanSetting(this, "Only Player", true)
        );
    }

    @Override
    public void onEnable() {
        path.clear();
        target = null;
        super.onEnable();
    }

    @Listen
    public void onMotion(MotionEvent event) {
        List<Entity> targets = mc.world.getLoadedEntityList()
                .stream()
                .filter(new EntityUtil.LivingFilter())
                .sorted(new EntityUtil.RangeSorter())
                .collect(Collectors.toList());

        LinkedList<Entity> entities = new LinkedList<>(targets);
        entities.sort(new EntityUtil.RangeSorter());

        if(targets == null) return;

        for (Entity entity : entities) {
            double deltaX = mc.player.posX - entity.posX;
            double deltaY = mc.player.posY - entity.posY;
            double deltaZ = mc.player.posZ - entity.posZ;
            if (mc.player == entity || onlyPlayer.get() && !(entity instanceof EntityPlayer) || !(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) <= range.get().doubleValue() || !(entity instanceof EntityLivingBase)))
                continue;

            target = (EntityLivingBase) entity;

            if(target == null) return;

            if (mc.player.ticksExisted % 1 == 0) {
                List<Vec3> blinkPath = PathUtil.findBlinkPath(target.posX, target.posY, target.posZ, 8.0);
                path.clear();
                if (mc.player.floatingTickCount <= (double) (79 - blinkPath.size() * 2)) {
                    path.add(mc.player.getPositionVector());
                    path.addAll(blinkPath);
                    for (Vec3 vec3 : path) {
                        mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord, false));
                    }
                    mc.player.swingItem();
                    mc.playerController.attackEntity(mc.player, target);
                    for (Vec3 vec3 : Lists.reverse(path)) {
                        mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord, false));
                    }
                }
            }
            return;
        }
        target = null;
        path.clear();
    }
}