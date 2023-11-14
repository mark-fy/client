package wtf.tophat.client.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.handler.PlayerHandler;
import wtf.tophat.client.events.impl.render.Render3DEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.render.ColorUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

@ModuleInfo(name = "Trajectories", desc = "you can see where your projectiles will hit", category = Module.Category.RENDER)
public class Trajectories extends Module {
    private final ArrayList<Vec3> positions = new ArrayList<>();

    private final StringSetting color;
    private final NumberSetting red, green, blue, red1, green1, blue1, darkFactor;
    public final NumberSetting lineWidth;

    public Trajectories() {
        TopHat.settingManager.add(
                lineWidth = new NumberSetting(this, "Width", 0.1, 10, 5, 1),
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Fade", "Astolfo", "Rainbow"),
                red = new NumberSetting(this, "Red", 0, 255, 95, 0).setHidden(() -> (!color.is("Gradient") && !color.is("Fade"))),
                green = new NumberSetting(this, "Green", 0, 255, 61, 0).setHidden(() -> (!color.is("Gradient") && !color.is("Fade"))),
                blue = new NumberSetting(this, "Blue", 0, 255, 248, 0).setHidden(() -> (!color.is("Gradient") && !color.is("Fade"))),
                red1 = new NumberSetting(this, "Second Red", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                green1 = new NumberSetting(this, "Second Green", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                blue1 = new NumberSetting(this, "Second Blue", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                darkFactor = new NumberSetting(this, "Dark Factor", 0, 1, 0.49, 2).setHidden(() -> !color.is("Fade"))
        );
    }

    @Listen
    public void onRender(Render3DEvent event) {
        int color = 0;
        int counter = 0;
        switch (this.color.get()) {
            case "Gradient":
                color = ColorUtil.fadeBetween(new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue()).getRGB(), new Color(red1.get().intValue(), green1.get().intValue(), blue1.get().intValue()).getRGB(), counter * 150L);
                break;
            case "Fade":
                int firstColor = new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue()).getRGB();
                color = ColorUtil.fadeBetween(firstColor, ColorUtil.darken(firstColor, darkFactor.get().floatValue()), counter * 150L);
                break;
            case "Rainbow":
                color = ColorUtil.getRainbow(3000, (int) (counter * 150L));
                break;
            case "Astolfo":
                color = ColorUtil.blendRainbowColours(counter * 150L);
                break;
        }

        this.positions.clear();
        ItemStack itemStack = mc.player.getCurrentEquippedItem();
        MovingObjectPosition m = null;
        if (itemStack != null && (itemStack.getItem() instanceof ItemSnowball || itemStack.getItem() instanceof ItemEgg || itemStack.getItem() instanceof ItemBow || itemStack.getItem() instanceof ItemEnderPearl)) {
            EntityLivingBase thrower = mc.player;
            float rotationYaw = PlayerHandler.prevYaw + (getYaw() - PlayerHandler.prevYaw) * mc.timer.renderPartialTicks;
            float rotationPitch = PlayerHandler.prevPitch + (getPitch() - PlayerHandler.prevPitch) * mc.timer.renderPartialTicks;
            double posX = thrower.lastTickPosX + (thrower.posX - thrower.lastTickPosX) * mc.timer.renderPartialTicks;
            double posY = thrower.lastTickPosY + thrower.getEyeHeight() + (thrower.posY - thrower.lastTickPosY) * mc.timer.renderPartialTicks;
            double posZ = thrower.lastTickPosZ + (thrower.posZ - thrower.lastTickPosZ) * mc.timer.renderPartialTicks;
            posX -= (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
            posY -= 0.10000000149011612D;
            posZ -= (MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
            float multipicator = 0.4F;
            if (itemStack.getItem() instanceof ItemBow) {
                multipicator = 1;
            }
            double motionX = (-MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * multipicator);
            double motionZ = (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * multipicator);
            double motionY = (-MathHelper.sin((rotationPitch) / 180.0F * (float) Math.PI) * multipicator);

            double x = motionX;
            double y = motionY;
            double z = motionZ;
            float inaccuracy = 0;
            float velocity = 1.5F;
            if (itemStack.getItem() instanceof ItemBow) {
                int i = getPlayer().getCurrentEquippedItem().getMaxItemUseDuration() - getPlayer().getItemInUseCount();
                float f = (float) i / 20.0F;
                f = (f * f + f * 2.0F) / 3.0F;

                if (f > 1.0F) {
                    f = 1.0F;
                }
                velocity = f * 2.0F * 1.5F;
            }

            Random rand = new Random();
            float ff = MathHelper.sqrt_double(x * x + y * y + z * z);
            x = x / (double) ff;
            y = y / (double) ff;
            z = z / (double) ff;
            x = x + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            y = y + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            z = z + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            x = x * (double) velocity;
            y = y * (double) velocity;
            z = z * (double) velocity;
            motionX = x;
            motionY = y;
            motionZ = z;
            float prevRotationYaw = (float) (MathHelper.atan2(x, z) * 180.0D / Math.PI);
            float prevRotationPitch = (float) (MathHelper.atan2(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0D / Math.PI);

            boolean b = true;
            int ticksInAir = 0;
            while (b) {
                if (ticksInAir > 300) {
                    b = false;
                }
                ticksInAir++;
                Vec3 vec3 = new Vec3(posX, posY, posZ);
                Vec3 vec31 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                MovingObjectPosition movingobjectposition = mc.world.rayTraceBlocks(vec3, vec31);
                vec3 = new Vec3(posX, posY, posZ);
                vec31 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                if (movingobjectposition != null) {
                    vec31 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
                }
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity != mc.player && entity instanceof EntityLivingBase) {
                        float f = 0.3F;
                        AxisAlignedBB localAxisAlignedBB = entity.getEntityBoundingBox().expand(f, f, f);
                        MovingObjectPosition localMovingObjectPosition = localAxisAlignedBB.calculateIntercept(vec3, vec31);
                        if (localMovingObjectPosition != null) {
                            movingobjectposition = localMovingObjectPosition;
                            break;
                        }
                    }
                }
                if (movingobjectposition != null) {
                    b = false;
                }
                m = movingobjectposition;

                posX += motionX;
                posY += motionY;
                posZ += motionZ;

                float f1 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
                rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * 180.0D / Math.PI);

                for (rotationPitch = (float) (MathHelper.atan2(motionY, f1) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)

                    while (rotationPitch - prevRotationPitch >= 180.0F) {
                        prevRotationPitch += 360.0F;
                    }

                while (rotationYaw - prevRotationYaw < -180.0F) {
                    prevRotationYaw -= 360.0F;
                }

                while (rotationYaw - prevRotationYaw >= 180.0F) {
                    prevRotationYaw += 360.0F;
                }
                float f2 = 0.99F;
                float f3 = 0.03F;
                if (itemStack.getItem() instanceof ItemBow) {
                    f3 = 0.05F;
                }
                motionX *= f2;
                motionY *= f2;
                motionZ *= f2;
                motionY -= f3;
                this.positions.add(new Vec3(posX, posY, posZ));
            }
            if (this.positions.size() > 1) {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GlStateManager.disableCull();
                GL11.glDepthMask(false);
                Color color2 = new Color(color);
                GL11.glColor4f(color2.getRed() / 255f, color2.getGreen() / 255f, color2.getBlue() / 255f, 0.7f);
                GL11.glLineWidth((float) (lineWidth.get().doubleValue() / 2f));
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                worldrenderer.begin(3, DefaultVertexFormats.POSITION);
                for (Vec3 vec3 : this.positions) {
                    worldrenderer.pos((float) vec3.xCoord - mc.getRenderManager().renderPosX, (float) vec3.yCoord - mc.getRenderManager().renderPosY, (float) vec3.zCoord - mc.getRenderManager().renderPosZ).endVertex();
                }
                tessellator.draw();

                if (m != null) {
                    GL11.glColor4f(color2.getRed() / 255f, color2.getGreen() / 255f, color2.getBlue() / 255f, 0.3f);
                    Vec3 hitVec = m.hitVec;
                    EnumFacing enumFacing1 = m.sideHit;
                    float minX = (float) (hitVec.xCoord - mc.getRenderManager().renderPosX);
                    float maxX = (float) (hitVec.xCoord - mc.getRenderManager().renderPosX);
                    float minY = (float) (hitVec.yCoord - mc.getRenderManager().renderPosY);
                    float maxY = (float) (hitVec.yCoord - mc.getRenderManager().renderPosY);
                    float minZ = (float) (hitVec.zCoord - mc.getRenderManager().renderPosZ);
                    float maxZ = (float) (hitVec.zCoord - mc.getRenderManager().renderPosZ);
                    if (enumFacing1 == EnumFacing.SOUTH) {
                        minX -= 0.4;
                        maxX += 0.4;
                        minY -= 0.4;
                        maxY += 0.4;
                        maxZ += 0.02;
                        minZ += 0.05;
                    } else if (enumFacing1 == EnumFacing.NORTH) {
                        minX -= 0.4;
                        maxX += 0.4;
                        minY -= 0.4;
                        maxY += 0.4;
                        maxZ -= 0.02;
                        minZ -= 0.05;
                    } else if (enumFacing1 == EnumFacing.EAST) {
                        maxX += 0.02;
                        minX += 0.05;
                        minY -= 0.4;
                        maxY += 0.4;
                        minZ -= 0.4;
                        maxZ += 0.4;
                    } else if (enumFacing1 == EnumFacing.WEST) {
                        maxX -= 0.02;
                        minX -= 0.05;
                        minY -= 0.4;
                        maxY += 0.4;
                        minZ -= 0.4;
                        maxZ += 0.4;
                    } else if (enumFacing1 == EnumFacing.UP) {
                        minX -= 0.4;
                        maxX += 0.4;
                        maxY += 0.02;
                        minY += 0.05;
                        minZ -= 0.4;
                        maxZ += 0.4;
                    } else if (enumFacing1 == EnumFacing.DOWN) {
                        minX -= 0.4;
                        maxX += 0.4;
                        maxY -= 0.02;
                        minY -= 0.05;
                        minZ -= 0.4;
                        maxZ += 0.4;
                    }

                    worldrenderer.begin(7, DefaultVertexFormats.POSITION);
                    //Side 1
                    worldrenderer.pos(minX, minY, minZ).endVertex();
                    worldrenderer.pos(minX, minY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, minZ).endVertex();
                    //Side 2
                    worldrenderer.pos(minX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, maxY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, maxZ).endVertex();
                    //Side 3
                    worldrenderer.pos(maxX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, minZ).endVertex();
                    worldrenderer.pos(maxX, maxY, minZ).endVertex();
                    worldrenderer.pos(maxX, maxY, maxZ).endVertex();
                    //Side 4
                    worldrenderer.pos(maxX, minY, minZ).endVertex();
                    worldrenderer.pos(minX, minY, minZ).endVertex();
                    worldrenderer.pos(minX, maxY, minZ).endVertex();
                    worldrenderer.pos(maxX, maxY, minZ).endVertex();
                    //Bottom
                    worldrenderer.pos(minX, minY, minZ).endVertex();
                    worldrenderer.pos(minX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, minZ).endVertex();
                    //Top
                    worldrenderer.pos(minX, maxY, minZ).endVertex();
                    worldrenderer.pos(minX, maxY, maxZ).endVertex();
                    worldrenderer.pos(maxX, maxY, maxZ).endVertex();
                    worldrenderer.pos(maxX, maxY, minZ).endVertex();

                    worldrenderer.endVertex();
                    tessellator.draw();
                    GL11.glLineWidth((float) 2);
                    worldrenderer.begin(3, DefaultVertexFormats.POSITION);

                    //Side 1
                    worldrenderer.pos(minX, minY, minZ).endVertex();
                    worldrenderer.pos(minX, minY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, minZ).endVertex();
                    worldrenderer.pos(minX, minY, minZ).endVertex();
                    worldrenderer.pos(maxX, minY, minZ).endVertex();
                    worldrenderer.pos(maxX, maxY, minZ).endVertex();
                    worldrenderer.pos(maxX, maxY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, minZ).endVertex();
                    worldrenderer.pos(maxX, minY, maxZ).endVertex();
                    worldrenderer.pos(minX, minY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, maxZ).endVertex();
                    worldrenderer.pos(maxX, maxY, maxZ).endVertex();
                    worldrenderer.pos(maxX, maxY, minZ).endVertex();
                    worldrenderer.pos(minX, maxY, minZ).endVertex();

                    worldrenderer.endVertex();
                    tessellator.draw();
                }
                GL11.glLineWidth((float) 1);
                GL11.glColor4f(1f, 1f, 1f, 1f);
                GL11.glDepthMask(true);
                GlStateManager.enableCull();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
            }
        }

    }

    @Override
    public void onDisable() {
        this.positions.clear();
    }
}