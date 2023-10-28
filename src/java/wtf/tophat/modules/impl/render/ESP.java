package wtf.tophat.modules.impl.render;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import wtf.tophat.events.impl.Render3DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.utilities.render.ColorUtil;

import static wtf.tophat.utilities.render.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.render.Colors.WHITE_COLOR;

@ModuleInfo(name = "ESP", desc = "render box around entities", category = Module.Category.RENDER)
public class ESP extends Module {

    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final Frustum frustrum = new Frustum();

    @Listen
    public void onRender3D(Render3DEvent eventRender){
        int counter = 0;

        for (Object object : Minecraft.getMinecraft().world.loadedEntityList) {
            EntityLivingBase ent;
            Entity entity = (Entity)object;
            if (!(entity instanceof EntityLivingBase) || entity.isInvisible() || (ent = (EntityLivingBase)entity) == mc.player || !this.isInViewFrustrum(ent)) continue;
            double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)mc.timer.renderPartialTicks;
            double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)mc.timer.renderPartialTicks;
            double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)mc.timer.renderPartialTicks;
            double finalWidth = (double)entity.width / 1.4;
            double finalHeight = (double)entity.height + (entity.isSneaking() ? -0.3 : 0.2);
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(posX - finalWidth - 0.1, posY - 0.1, posZ - finalWidth - 0.1, posX + finalWidth + 0.1, posY + finalHeight + 0.1, posZ + finalWidth + 0.1);
            List<Vector3d> vectorList = Arrays.asList(new Vector3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ));
            mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
            Vector4d posVec = null;
            for (Vector3d vector : vectorList) {
                FloatBuffer otherVec = GLAllocation.createDirectFloatBuffer(4);
                GL11.glGetFloat(2982, modelview);
                GL11.glGetFloat(2983, projection);
                GL11.glGetInteger(2978, viewport);
                if (GLU.gluProject((float)(vector.x - mc.getRenderManager().viewerPosX), (float)((double)((float)vector.y) - mc.getRenderManager().viewerPosY), (float)((double)((float)vector.z) - mc.getRenderManager().viewerPosZ), modelview, projection, viewport, otherVec)) {
                    vector = new Vector3d(otherVec.get(0) / (float)new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor(), ((float) Display.getHeight() - otherVec.get(1)) / (float)new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor(), otherVec.get(2));
                }
                if (!(vector.z >= 0.0) || !(vector.z < 1.0)) continue;
                if (posVec == null) {
                    posVec = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                }
                posVec.x = Math.min(vector.x, posVec.x);
                posVec.y = Math.min(vector.y, posVec.y);
                posVec.z = Math.max(vector.x, posVec.z);
                posVec.w = Math.max(vector.y, posVec.w);
            }
            mc.entityRenderer.setupOverlayRendering();
            if (posVec == null) continue;
            GL11.glPushMatrix();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.enableBlend();
            float x = (float)posVec.x;
            float w = (float)posVec.z - x;
            float y = (float)posVec.y;
            float h = (float)posVec.w - y;
            if (ent instanceof EntityPlayer && !mc.player.isInvisibleToPlayer(mc.player)) {
                this.drawBorderedRect(x, y, w, h, 1.0, ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L), 0);
                this.drawBorderedRect(x, y, w, h, 0.5, ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L), 0);
                counter++;
            }
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.disablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
            GlStateManager.popMatrix();
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

    public void drawBorderedRect(double x, double y, double width, double height, double lineSize, int borderColor, int color) {
        Gui.drawRect3(x, y, x + width, y + height, color);
        Gui.drawRect3(x, y, x + width, y + lineSize, borderColor);
        Gui.drawRect3(x, y, x + lineSize, y + height, borderColor);
        Gui.drawRect3(x + width, y, x + width - lineSize, y + height, borderColor);
        Gui.drawRect3(x, y + height, x + width, y + height - lineSize, borderColor);
    }

}
