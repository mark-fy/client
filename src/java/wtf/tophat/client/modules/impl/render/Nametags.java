package wtf.tophat.client.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.Render3DEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.render.shaders.RoundedUtil;

import java.awt.*;

@ModuleInfo(name = "Nametags", desc = "renders better nametags", category = Module.Category.RENDER)
public class Nametags extends Module {

    // Hook in Render.java -> renderOffsetLivingLabel method

    private final NumberSetting scaling;
    private final BooleanSetting invisibles, smartScale;

    public Nametags() {
        TopHat.settingManager.add(
            invisibles = new BooleanSetting(this, "Invisibles", false),
            smartScale = new BooleanSetting(this, "Smart Scale", false),
            scaling = new NumberSetting(this, "Scale", 0.1, 2, 2, 2)
        );
    }

    @Listen
    public void onRender3D(Render3DEvent event) {
        try {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (player.equals(mc.player) || !player.isEntityAlive() || player.isInvisible() && !invisibles.get()) {
                    continue;
                }
                double x = this.interpolate(player.lastTickPosX, player.posX, event.getPartialTicks()) - mc.getRenderManager().renderPosX;
                double y = this.interpolate(player.lastTickPosY, player.posY, event.getPartialTicks()) - mc.getRenderManager().renderPosY;
                double z = this.interpolate(player.lastTickPosZ, player.posZ, event.getPartialTicks()) - mc.getRenderManager().renderPosZ;
                this.renderNameTag(player, x, y + 0.1, z, event.getPartialTicks());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += player.isSneaking() ? 0.5 : 0.7;
        Entity camera = mc.getRenderViewEntity();
        assert (camera != null);
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = ("§c") + player.getName();
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = mc.fontRenderer.getStringWidth(displayTag) / 2;
        int height = mc.fontRenderer.FONT_HEIGHT;
        double scale = (scaling.get().doubleValue() * (distance * 11)) / 1050d;
        if (distance <= 8.0 && this.smartScale.get()) {
            scale = 0.0245d;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4f, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.settings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();

        GlStateManager.disableBlend();
        RoundedUtil.drawRound(-width-5, -15, width*2+8, height+4, 0, new Color(0, 0, 0, 179));
        mc.fontRenderer.drawStringWithShadow(displayTag, -width - 1, -12, this.getDisplayColour(player));
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private int getDisplayColour(EntityPlayer player) {
        int colour = -new Color(197, 197, 197).getRGB();
        if (player.isInvisible()) {
            colour = -1113785;
        } else if (player.isSneaking()) {
            colour = -new Color(252, 234, 93).getRGB();
        }
        return colour;
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }
}