package wtf.tophat.client.modules.impl.combat;

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
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.entity.EntityUtil;
import wtf.tophat.client.utilities.misc.PathUtil;

@ModuleInfo(name = "Teleport Aura", desc = "attack entities with infinity range", category = Module.Category.COMBAT)
public class TeleportAura extends Module {

    public EntityLivingBase target;
    public final List<Vec3> path = new ArrayList<>();

    private final NumberSetting range;
    private final NumberSetting delay;
    private final BooleanSetting onlyPlayer;

    public TeleportAura() {
        TopHat.settingManager.add(
                range = new NumberSetting(this, "Range", 4.0, 100.0, 49.0, 1),
                delay = new NumberSetting(this, "Delay", 1, 60, 1, 1),
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
        List<Entity> targets = this.mc.world.getLoadedEntityList()
                .stream()
                .filter(new EntityUtil.LivingFilter())
                .sorted(new EntityUtil.RangeSorter())
                .collect(Collectors.toList());

        LinkedList<Entity> entities = new LinkedList<>(targets);
        entities.sort(new EntityUtil.RangeSorter());

        for (Entity entity : entities) {
            double deltaX = this.mc.player.posX - entity.posX;
            double deltaY = this.mc.player.posY - entity.posY;
            double deltaZ = this.mc.player.posZ - entity.posZ;
            if (this.mc.player == entity || onlyPlayer.get() && !(entity instanceof EntityPlayer) || !(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) <= range.get().doubleValue()) || !(entity instanceof EntityLivingBase))
                continue;
            this.target = (EntityLivingBase) entity;
            if (this.mc.player.ticksExisted % delay.get().doubleValue() == 0) {
                List<Vec3> blinkPath = PathUtil.findBlinkPath(this.target.posX, this.target.posY, this.target.posZ, 8.0);
                this.path.clear();
                if (this.mc.player.floatingTickCount <= (double) (79 - blinkPath.size() * 2)) {
                    this.path.add(this.mc.player.getPositionVector());
                    this.path.addAll(blinkPath);
                    for (Vec3 vec3 : this.path) {
                        this.mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord, false));
                    }
                    this.mc.player.swingItem();
                    this.mc.playerController.attackEntity(this.mc.player, this.target);
                    for (Vec3 vec3 : Lists.reverse(this.path)) {
                        this.mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord, false));
                    }
                }
            }
            return;
        }
        this.target = null;
        this.path.clear();
    }
}