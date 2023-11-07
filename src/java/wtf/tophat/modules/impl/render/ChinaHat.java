package wtf.tophat.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.Render3DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.shaders.RenderUtil;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "China Hat", desc = "Render a Chinese hat", category = Module.Category.RENDER)
public class ChinaHat extends Module {

    private final StringSetting color;
    private final NumberSetting red, green, blue, red1, green1, blue1, darkFactor;
    private final BooleanSetting showInFirstPerson;

    public ChinaHat() {
        TopHat.settingManager.add(
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Fade", "Astolfo", "Rainbow"),
                red = new NumberSetting(this, "Red", 0, 255, 95, 0).setHidden(() -> !color.is("Gradient") && !color.is("Fade")),
                green = new NumberSetting(this, "Green", 0, 255, 61, 0).setHidden(() -> !color.is("Gradient") && !color.is("Fade")),
                blue = new NumberSetting(this, "Blue", 0, 255, 248, 0).setHidden(() -> !color.is("Gradient") && !color.is("Fade")),
                red1 = new NumberSetting(this, "Second Red", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                green1 = new NumberSetting(this, "Second Green", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                blue1 = new NumberSetting(this, "Second Blue", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                darkFactor = new NumberSetting(this, "Dark Factor", 0, 1, 0.49, 2).setHidden(() -> !color.is("Fade")),
                showInFirstPerson =  new BooleanSetting(this, "Show In First Person", true)
        );
    }



    @Listen
    public void onRender3D(Render3DEvent event){
        if (mc.settings.thirdPersonView == 0 && !showInFirstPerson.get()) return;

        int counter = 0;
        int rcColor = 0;

        switch (this.color.get()) {
            case "Gradient":
                rcColor = ColorUtil.fadeBetween(new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue()).getRGB(), new Color(red1.get().intValue(), green1.get().intValue(), blue1.get().intValue()).getRGB(), counter * 150L);
                break;
            case "Fade":
                int firstColor = new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue()).getRGB();
                rcColor = ColorUtil.fadeBetween(firstColor, ColorUtil.darken(firstColor, darkFactor.get().floatValue()), counter * 150L);
                break;
            case "Rainbow":
                rcColor = ColorUtil.getRainbow(3000, (int) (counter * 150L));
                break;
            case "Astolfo":
                rcColor = ColorUtil.blendRainbowColours(counter * 150L);
                break;
        }

        final double x = mc.player.lastTickPosX +
                (mc.player.posX - mc.player.lastTickPosX) * mc.timer.renderPartialTicks -
                mc.getRenderManager().viewerPosX;
        final double y = (mc.player.lastTickPosY +
                (mc.player.posY - mc.player.lastTickPosY) * mc.timer.renderPartialTicks -
                mc.getRenderManager().viewerPosY
        ) + mc.player.getEyeHeight() + 0.5 + (mc.player.isSneaking() ? -0.2 : 0);
        final double z = mc.player.lastTickPosZ +
                (mc.player.posZ - mc.player.lastTickPosZ) * mc.timer.renderPartialTicks -
                mc.getRenderManager().viewerPosZ;

        for (int i = 0; i < 360; i++) {
            float dX = (0.75f * (float) Math.cos(Math.toRadians(i)));
            float dZ = (0.75f * (float) Math.sin(Math.toRadians(i)));
            GlStateManager.pushMatrix();
            RenderUtil.draw3dLine(x, y, z, dX, y - 0.2, dZ, new Color(rcColor));
            counter++;
            GlStateManager.popMatrix();
        }
    }
}
