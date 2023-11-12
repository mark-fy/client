package wtf.tophat.client.modules.impl.hud;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.ScaledResolution;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.render.Render2DEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import java.awt.Color;
import java.util.Collection;

import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
@ModuleInfo(name = "Potion HUD", desc = "shows actives potions", category = Module.Category.HUD)
public class PotionHUD extends Module {

    @Listen
    public void onRender2D(Render2DEvent event) {
        if(TopHat.moduleManager.getByClass(Watermark.class).mode.is("GameSense") || TopHat.moduleManager.getByClass(Watermark.class).mode.is("Exhibition"))
            return;
        drawPotionStatus(event.getScaledResolution());
    }

    private void drawPotionStatus(ScaledResolution sr) {
        int y = 0;
        for (final PotionEffect effect : (Collection<PotionEffect>) mc.player.getActivePotionEffects()) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            String PType = I18n.format(potion.getName());
            switch (effect.getAmplifier()) {
                case 1:
                    PType = PType + " II";
                    break;
                case 2:
                    PType = PType + " III";
                    break;
                case 3:
                    PType = PType + " IV";
                    break;
                default:
                    break;
            }
            if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                PType = PType + "\2477:\2476 " + Potion.getDurationString(effect);
            } else if (effect.getDuration() < 300) {
                PType = PType + "\2477:\247c " + Potion.getDurationString(effect);
            } else if (effect.getDuration() > 600) {
                PType = PType + "\2477:\2477 " + Potion.getDurationString(effect);
            }
            int ychat = mc.ingameGUI.getChatGUI().getChatOpen() ? 14 : 2;
            mc.fontRenderer.drawStringWithShadow(PType, sr.getScaledWidth() - mc.fontRenderer.getStringWidth(PType) - 3, sr.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT + y - ychat, new Color(255, 255, 255).getRGB());
            y -= 10;
        }
    }

}
