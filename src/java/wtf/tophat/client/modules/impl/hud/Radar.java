package wtf.tophat.client.modules.impl.hud;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import org.lwjgl.opengl.GL11;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.render.Render2DEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.modules.impl.render.PostProcessing;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.render.ColorUtil;
import wtf.tophat.client.utilities.render.shaders.RenderUtil;
import wtf.tophat.client.utilities.render.shaders.RoundedUtil;
import wtf.tophat.client.utilities.render.shaders.bloom.KawaseBloom;
import wtf.tophat.client.utilities.render.shaders.blur.GaussianBlur;

import java.awt.*;

@ModuleInfo(
        name = "Radar",
        category = Module.Category.HUD,
        desc = "Shows player locations on the hud"
)
public class Radar extends Module {

    public BooleanSetting players = new BooleanSetting(this,"Players", true);
    public BooleanSetting monsters = new BooleanSetting(this,"Monsters",  false);
    public BooleanSetting animals = new BooleanSetting(this,"Animals",  false);
    public BooleanSetting invisible = new BooleanSetting(this,"Invisibles",  false);

    public Radar(){
        TopHat.settingManager.add(
                players,
                monsters,
                animals,
                invisible
        );
    }

    @Listen
    public void onRender2D(Render2DEvent event) {
        if (mc.world == null || mc.player == null) {
            return;
        }

        int counter = 0;

        int miX = 5, miY = 70;
        int maX = miX + 100, maY = miY + 100;
        GL11.glPushMatrix();

        if (TopHat.moduleManager.getByClass(PostProcessing.class).isEnabled() && TopHat.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
            GaussianBlur.startBlur();
            RoundedUtil.drawRound(miX, miY, 100, maY - miY, 3, new Color(0, 0, 0, 100));
            GaussianBlur.endBlur(3, 2);
        } else {
            RoundedUtil.drawRound(miX, miY, 100, maY - miY, 3, new Color(0, 0, 0, 128));
        }

        RenderUtil.drawRect(miX, miY - 1, 100, 1, ColorUtil.blendRainbowColours(counter * 150L));
        int borderWidth = 2;

        GL11.glPushMatrix();

        if (TopHat.moduleManager.getByClass(PostProcessing.class).isEnabled() && TopHat.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
            GaussianBlur.startBlur();
            RoundedUtil.drawRound(miX - borderWidth, miY - borderWidth, 100 + 2 * borderWidth, (maY - miY) + 2 * borderWidth, 3, new Color(0, 0, 0, 100));
            GaussianBlur.endBlur(3, 1);
        } else {
            RoundedUtil.drawRound(miX - borderWidth, miY - borderWidth, 100 + 2 * borderWidth, (maY - miY) + 2 * borderWidth, 3, new Color(0, 0, 0, 100));
        }

        RenderUtil.drawRect(miX - borderWidth, miY - borderWidth - 1, 100 + 2 * borderWidth, borderWidth, ColorUtil.blendRainbowColours(counter * 150L)); // Bordure supérieure
        RenderUtil.drawRect(miX - borderWidth, maY + borderWidth, 100 + 2 * borderWidth, borderWidth, ColorUtil.blendRainbowColours(counter * 150L)); // Bordure inférieure
        RenderUtil.drawRect(miX - borderWidth - 1, miY - borderWidth, borderWidth, (maY - miY) + 2 * borderWidth, ColorUtil.blendRainbowColours(counter * 150L)); // Bordure gauche
        RenderUtil.drawRect(maX, miY - borderWidth, borderWidth, (maY - miY) + 2 * borderWidth, ColorUtil.blendRainbowColours(counter * 150L)); // Bordure droite

        GL11.glPopMatrix();

        counter++;
        RenderUtil.draw2DPolygon(maX / 2 + 3, miY + 52, 5f, 3, -1);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scale = new ScaledResolution(mc).getScaleFactor();
        GL11.glScissor(miX * scale, mc.displayHeight - scale * 170, maX * scale - (scale * 5), scale * 100);

        for (Entity en : mc.world.loadedEntityList) {
            if (en instanceof EntityMob && !monsters.get()) continue;
            if (en instanceof EntityAnimal && !animals.get()) continue;
            if (en instanceof EntityVillager && !animals.get()) continue;
            if (en.isInvisible() && !invisible.get()) continue;
            if (en == mc.player) continue;

            double dist_sq = mc.player.getDistanceSqToEntity(en);
            if (dist_sq > 360) {
                continue;
            }
            double x = en.posX - mc.player.posX, z = en.posZ - mc.player.posZ;
            double calc = Math.atan2(x, z) * 57.2957795131f;
            double angle = ((mc.player.rotationYaw + calc) % 360) * 0.01745329251f;
            double hypotenuse = dist_sq / 5;
            double x_shift = hypotenuse * Math.sin(angle), y_shift = hypotenuse * Math.cos(angle);
            RenderUtil.draw2DPolygon(maX / 2 + 3 - x_shift, miY + 52 - y_shift, 3f, 4, Color.red.getRGB());
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

}