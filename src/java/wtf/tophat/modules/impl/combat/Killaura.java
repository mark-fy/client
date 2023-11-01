package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import org.lwjgl.opengl.GL11;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.Render3DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.player.Scaffold;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.entity.EntityUtil;
import wtf.tophat.utilities.math.MathUtil;
import wtf.tophat.utilities.misc.RayCast;
import wtf.tophat.utilities.player.rotations.RotationUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.shaders.RenderUtil;
import wtf.tophat.utilities.time.TimeUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "Kill Aura", desc = "kills entities", category = Module.Category.COMBAT)
public class Killaura extends Module {

    public final TimeUtil timer = new TimeUtil();
    public final StringSetting sort, autoblockMode;
    public final NumberSetting minCps, maxCps, minRange, maxRange;
    public final BooleanSetting render, inGUI;

    public static EntityLivingBase target = null;

    public List<EntityLivingBase> targets = new ArrayList<>();

    public Killaura(){
        Client.settingManager.add(
                sort = new StringSetting(this, "Sort", "Distance", "Distance", "Health"),
                autoblockMode = new StringSetting(this, "Auto Block", "None", "None", "Vanilla", "Intave", "AAC", "NCP", "Grim"),
                minCps = new NumberSetting(this, "Min CPS", 1, 20, 12, 1),
                maxCps = new NumberSetting(this, "Max CPS", 1, 20, 17, 1),
                minRange = new NumberSetting(this, "Min Range", 0, 6, 3.4, 1),
                maxRange = new NumberSetting(this, "Max Range", 1, 6, 3.5, 1),
                inGUI = new BooleanSetting(this, "Attack in GUI", false),
                render = new BooleanSetting(this, "Target ES", true)
        );
    }

    private void targetsSort() {
        switch (sort.get()){
            case "Health":
                this.targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;
            case "Distance":
                this.targets.sort((o1, o2) -> (int)(o1.getDistanceToEntity(mc.player) - o2.getDistanceToEntity(mc.player)));
                break;
        }
    }

    @Listen
    public void onMotion(MotionEvent e) {
        if (Client.moduleManager.getByClass(Scaffold.class).isEnabled())
            return;

        target = EntityUtil.getClosestEntity(minRange.get().doubleValue(), maxRange.get().doubleValue());

        if (target != null && (inGUI.get() && mc.currentScreen == null)) {
            if (e.getState() == Event.State.PRE) {
                if (!Client.moduleManager.getByClass(Scaffold.class).isEnabled()) {
                    EntityLivingBase p = target = (EntityLivingBase) RayCast.raycast(mc, minRange.get().doubleValue(), maxRange.get().doubleValue(), getTarget());
                    if (p == null)
                        return;

                    float[] rotations = RotationUtil.getRotation(target);

                    e.setYaw(rotations[0]);
                    e.setPitch(rotations[1]);

                    targetsSort();
                    if (timer.elapsed((long) (1000.0D / MathUtil.randomNumber(minCps.get().doubleValue(), maxCps.get().doubleValue())))) {
                        mc.player.swingItem();
                        mc.player.sendQueue.send(new C02PacketUseEntity(p, C02PacketUseEntity.Action.ATTACK));

                        ItemStack currentItem = mc.player.getHeldItem();
                        switch (autoblockMode.get()) {
                            case "Vanilla":
                                mc.playerController.sendUseItem(getPlayer(), getWorld(), currentItem);
                                break;
                            case "NCP":
                                mc.player.setItemInUse(currentItem, 32767);
                                break;
                            case "AAC":
                                if (mc.player.ticksExisted % 2 == 0) {
                                    mc.playerController.interactWithEntitySendPacket(getPlayer(), p);
                                    mc.player.sendQueue.send(new C08PacketPlayerBlockPlacement(currentItem));
                                }
                                break;
                            case "Grim":
                            case "Intave":
                                mc.playerController.interactWithEntitySendPacket(getPlayer(), p);
                                mc.player.sendQueue.send(new C08PacketPlayerBlockPlacement(currentItem));
                                break;
                        }

                        timer.reset();
                    }
                }
            }
        }
    }

    public Entity getTarget() {
        for (Entity o : mc.world.loadedEntityList) {
            if (o instanceof EntityPlayer && !(o instanceof EntityVillager)) {
                if (!Client.moduleManager.getByClass(Scaffold.class).isEnabled() && !o.isDead && o != mc.player) {
                    double distanceToEntity = mc.player.getDistanceToEntity(o);

                    if (distanceToEntity >= minRange.get().doubleValue() && distanceToEntity <= maxRange.get().doubleValue()) {
                        if (mc.player.canEntityBeSeen(o)) {
                            return o;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Listen
    public void onRender3D(Render3DEvent event){
        if(render.get()) {
            float partialTicks = mc.timer.renderPartialTicks;

            EntityLivingBase player = target;

            Color color = new Color(255, 255, 255);

            if (mc.getRenderManager() == null || player == null) return;

            double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks - (mc.getRenderManager()).renderPosX;
            double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + Math.sin(System.currentTimeMillis() / 2E+2) + 1 - (mc.getRenderManager()).renderPosY;
            double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks - (mc.getRenderManager()).renderPosZ;

            GL11.glPushMatrix();
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glEnable(2832);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glHint(3154, 4354);
            GL11.glHint(3155, 4354);
            GL11.glHint(3153, 4354);
            GL11.glDepthMask(false);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.disableCull();
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

            for (float i = 0; i <= Math.PI * 2 + ((Math.PI * 2) / 32.F); i += (Math.PI * 2) / 32.F) {
                double vecX = x + 0.67 * Math.cos(i);
                double vecZ = z + 0.67 * Math.sin(i);

                RenderUtil.color(ColorUtil.withAlpha(color, (int) (255 * 0.25)).getRGB());
                GL11.glVertex3d(vecX, y, vecZ);
            }

            for (float i = 0; i <= Math.PI * 2 + (Math.PI * 2) / 32.F; i += (Math.PI * 2) / 32.F) {
                double vecX = x + 0.67 * Math.cos(i);
                double vecZ = z + 0.67 * Math.sin(i);

                RenderUtil.color(ColorUtil.withAlpha(color, (int) (255 * 0.25)).getRGB());
                GL11.glVertex3d(vecX, y, vecZ);

                RenderUtil.color(ColorUtil.withAlpha(color, 0).getRGB());
                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
            }

            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableCull();
            GL11.glDisable(2848);
            GL11.glDisable(2848);
            GL11.glEnable(2832);
            GL11.glEnable(3553);
            GL11.glPopMatrix();
            RenderUtil.color(Color.WHITE.getRGB());
        }
    }
}