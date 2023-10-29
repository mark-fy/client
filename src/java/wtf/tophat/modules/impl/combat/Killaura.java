package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.events.impl.Render3DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.player.Scaffold;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.entity.EntityUtil;
import wtf.tophat.utilities.math.MathUtil;
import wtf.tophat.utilities.player.rotations.RotationUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.shaders.RenderUtil;
import wtf.tophat.utilities.time.Stopwatch;

import java.awt.*;
import java.util.ArrayList;

@ModuleInfo(name = "Killaura", desc = "kills entities", category = Module.Category.COMBAT)
public class Killaura extends Module {

    private final Stopwatch timer = new Stopwatch();
    private final StringSetting rotate;
    private final NumberSetting minCps, maxCps, minDistance, maxDistance;
    private final BooleanSetting render;

    private EntityLivingBase lastTarget;
    private double lastHealth;

    public static boolean blocking;

    public static ArrayList<EntityLivingBase> totalTargets = new ArrayList<EntityLivingBase>();

    public Killaura(){
        Client.settingManager.add(
                rotate = new StringSetting(this, "Rotate", "Pre", "Pre", "Post", "Dynamic"),
                minCps = new NumberSetting(this, "Min CPS", 1, 20, 12, 1),
                maxCps = new NumberSetting(this, "Max CPS", 1, 20, 17, 1),
                minDistance = new NumberSetting(this, "Min Range", 2, 6, 3.4, 1),
                maxDistance = new NumberSetting(this, "Max Range", 2, 6, 4.5, 1),
                render = new BooleanSetting(this, "Render", true)
        );
    }

    @Override
    public void onDisable() {
        blocking = false;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    public static EntityLivingBase target = null;

    @Listen
    public void onMotion(MotionEvent e) {
        if (Client.moduleManager.getByClass(Scaffold.class).isEnabled())
            return;
        double range = MathUtil.randomNumber(minDistance.get().doubleValue(), maxDistance.get().doubleValue());
        target = EntityUtil.getClosestEntity(range);

        if (target != null) {

            if(mc.getNetHandler().doneLoadingTerrain) {
                if (!totalTargets.contains(target)) {
                    totalTargets.add(target);
                }
            } else {
                totalTargets.clear();
            }

            float[] rotations = RotationUtil.getRotation(target);
            float rot0 = (float) (rotations[0] + MathUtil.randomNumber(-5, 5));
            float rot1 = (float) (rotations[1] + MathUtil.randomNumber(-8, 4));

            switch (rotate.get()){
                case "Pre":
                    if(e.getState() == Event.State.PRE) {
                        e.setYaw(rot0);
                        e.setPitch(rot1);
                    }
                    break;
                case "Post":
                    if(e.getState() == Event.State.POST) {
                        e.setYaw(rot0);
                        e.setPitch(rot1);
                    }
                    break;
                case "Dynamic":
                    if(e.getState() == Event.State.PRE && e.getState() == Event.State.POST){
                        e.setYaw(rot0);
                        e.setPitch(rot1);
                    }
                    break;
            }

            if (e.getState() == Event.State.PRE) {
                if (timer.timeElapsed(1000 / MathUtil.getRandInt(minCps.get().intValue(), maxCps.get().intValue()))) {
                    attack(target);
                    timer.resetTime();
                }
            }
        }
    }

    public void attack(Entity entity) {
        mc.player.swingItem();
        mc.playerController.attackEntity(mc.player, entity);
    }

    @Listen
    public void onRender3D(Render3DEvent event){
        if(render.get()) {
            final float partialTicks = mc.timer.renderPartialTicks;

            EntityLivingBase player = (EntityLivingBase) this.target;

            final Color color = new Color(255, 255, 255);

            if (mc.getRenderManager() == null || player == null) return;

            final double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks - (mc.getRenderManager()).renderPosX;
            final double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + Math.sin(System.currentTimeMillis() / 2E+2) + 1 - (mc.getRenderManager()).renderPosY;
            final double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks - (mc.getRenderManager()).renderPosZ;

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