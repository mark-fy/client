package wtf.tophat.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import wtf.tophat.events.impl.Render3DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.utilities.render.VisualHelper;


/**
 * @credit helium.rip
 */

@ModuleInfo(name = "HentaiESP", desc = "render hentai on entities", category = Module.Category.RENDER)
public class HentaiESP extends Module {

    private static final Frustum frustrum = new Frustum();
    private ResourceLocation lewd;

    @Listen
    public void onRender3D(Render3DEvent event) {
        this.lewd = new ResourceLocation("tophat/lewd.png");
        for (final EntityPlayer p : mc.player.getEntityWorld().playerEntities) {
            if (isInViewFrustrum(p) && !p.isInvisible() && p.isEntityAlive()) {
                if (p == mc.player) {
                    continue;
                }

                final double x = VisualHelper.interp(p.posX, p.lastTickPosX) - Minecraft.getMinecraft().getRenderManager().renderPosX;
                final double y = VisualHelper.interp(p.posY, p.lastTickPosY) - Minecraft.getMinecraft().getRenderManager().renderPosY;
                final double z = VisualHelper.interp(p.posZ, p.lastTickPosZ) - Minecraft.getMinecraft().getRenderManager().renderPosZ;
                GlStateManager.pushMatrix();
                GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
                GL11.glDisable(2929);
                final float distance = MathHelper.clamp_float(mc.player.getDistanceToEntity(p), 20.0f, Float.MAX_VALUE);
                final double scale = 0.005 * distance;
                GlStateManager.translate(x, y, z);
                GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                GlStateManager.scale(-0.1, -0.1, 0.0);
                Minecraft.getMinecraft().getTextureManager().bindTexture(this.lewd);
                Gui.drawScaledCustomSizeModalRect((int) (p.width / 2.0f - distance / 3.0f), (int) (-p.height - distance), 0.0f, 0.0f, (int) 1.0, (int) 1.0, (int) (252.0 * (scale / 2.0)), (int) (476.0 * (scale / 2.0)), 1.0f, 1.0f);
                GL11.glEnable(2929);
                GlStateManager.popMatrix();
            }
        }
    }

    public boolean isInViewFrustrum(Entity entity) {
        return this.isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    public boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }
}