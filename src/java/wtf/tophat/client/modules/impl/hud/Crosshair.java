package wtf.tophat.client.modules.impl.hud;

import java.awt.Color;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.Render2DEvent;
import wtf.tophat.client.events.impl.Render3DEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.render.ColorUtil;

@ModuleInfo(name = "Crosshair", desc = "changes your crosshair", category = Module.Category.HUD)
public class Crosshair extends Module {

    private final BooleanSetting outline, renderTop, renderBottom, renderLeft, renderRight;
    private final NumberSetting widthSetting, gapSetting, sizeSetting, red, green, blue, red1, green1, blue1, darkFactor;
    private final StringSetting color;

    public Crosshair(){
        TopHat.settingManager.add(
                outline = new BooleanSetting(this, "Outline", true),
                renderTop = new BooleanSetting(this, "Top", true),
                renderBottom = new BooleanSetting(this, "Bottom", true),
                renderLeft = new BooleanSetting(this, "Left", true),
                renderRight = new BooleanSetting(this, "Right", true),

                widthSetting = new NumberSetting(this, "Width", 0.25, 15.0, 1.0, 1),
                gapSetting = new NumberSetting(this, "Gap", 0.0, 15.0, 2.5, 1),
                sizeSetting = new NumberSetting(this, "Size", 0.25, 15.0, 2.5, 1),

                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Fade", "Astolfo", "Rainbow"),
                red = new NumberSetting(this, "Red", 0, 255, 95, 0).setHidden(() -> !color.is("Gradient") && !color.is("Fade")),
                green = new NumberSetting(this, "Green", 0, 255, 61, 0).setHidden(() -> !color.is("Gradient") && !color.is("Fade")),
                blue = new NumberSetting(this, "Blue", 0, 255, 248, 0).setHidden(() -> !color.is("Gradient") && !color.is("Fade")),
                red1 = new NumberSetting(this, "Second Red", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                green1 = new NumberSetting(this, "Second Green", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                blue1 = new NumberSetting(this, "Second Blue", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                darkFactor = new NumberSetting(this, "Dark Factor", 0 ,1, 0.49, 2).setHidden(() -> !color.is("Fade"))

        );
    }

    @Listen
    public void onRender2D(Render2DEvent e) {
        ScaledResolution sr = e.getScaledResolution();
        this.renderVerticalRects(sr);
        this.renderHorizontalRects(sr);
    };

    private void renderHorizontalRects(ScaledResolution sr) {
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


        float height = this.widthSetting.get().floatValue() / 2.0f;
        float gap = this.gapSetting.get().floatValue();
        float outlineSize = 0.5f;
        if (this.outline.get()) {
            if (this.renderLeft.get()) {
                Gui.drawRect3((float)(sr.getScaledWidth() / 2) - gap - this.sizeSetting.get().floatValue() - outlineSize, (float)(sr.getScaledHeight() / 2) - height - outlineSize, (float)(sr.getScaledWidth() / 2) - gap + outlineSize, (float)(sr.getScaledHeight() / 2) + height + outlineSize, Color.BLACK.getRGB());
            }
            if (this.renderRight.get()) {
                Gui.drawRect3((float)(sr.getScaledWidth() / 2) + gap - outlineSize, (float)(sr.getScaledHeight() / 2) - height - outlineSize, (float)(sr.getScaledWidth() / 2) + gap + this.sizeSetting.get().floatValue() + outlineSize, (float)(sr.getScaledHeight() / 2) + height + outlineSize, Color.BLACK.getRGB());
            }
        }
        if (this.renderLeft.get()) {
            Gui.drawRect3((float)(sr.getScaledWidth() / 2) - gap - this.sizeSetting.get().floatValue(), (float)(sr.getScaledHeight() / 2) - height, (float)(sr.getScaledWidth() / 2) - gap, (float)(sr.getScaledHeight() / 2) + height, rcColor);
        }
        if (this.renderRight.get()) {
            Gui.drawRect3((float)(sr.getScaledWidth() / 2) + gap, (float)(sr.getScaledHeight() / 2) - height, (float)(sr.getScaledWidth() / 2) + gap + this.sizeSetting.get().floatValue(), (float)(sr.getScaledHeight() / 2) + height, rcColor);
        }
    }

    private void renderVerticalRects(ScaledResolution sr) {
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

        float width = this.widthSetting.get().floatValue() / 2.0f;
        float gap = this.gapSetting.get().floatValue();
        float outlineSize = 0.5f;
        if (this.outline.get()) {
            if (this.renderTop.get()) {
                Gui.drawRect3((float)(sr.getScaledWidth() / 2) - width - outlineSize, (float)(sr.getScaledHeight() / 2) - gap - this.sizeSetting.get().floatValue() - outlineSize, (float)(sr.getScaledWidth() / 2) + width + outlineSize, (float)(sr.getScaledHeight() / 2) - gap + outlineSize, Color.BLACK.getRGB());
            }
            if (this.renderBottom.get()) {
                Gui.drawRect3((float)(sr.getScaledWidth() / 2) - width - outlineSize, (float)(sr.getScaledHeight() / 2) + gap + this.sizeSetting.get().floatValue() + outlineSize, (float)(sr.getScaledWidth() / 2) + width + outlineSize, (float)(sr.getScaledHeight() / 2) + gap - outlineSize, Color.BLACK.getRGB());
            }
        }
        if (this.renderTop.get()) {
            Gui.drawRect3((float)(sr.getScaledWidth() / 2) - width, (float)(sr.getScaledHeight() / 2) - gap - this.sizeSetting.get().floatValue(), (float)(sr.getScaledWidth() / 2) + width, (float)(sr.getScaledHeight() / 2) - gap, rcColor);
        }
        if (this.renderBottom.get()) {
            Gui.drawRect3((float)(sr.getScaledWidth() / 2) - width, (float)(sr.getScaledHeight() / 2) + gap + this.sizeSetting.get().floatValue(), (float)(sr.getScaledWidth() / 2) + width, (float)(sr.getScaledHeight() / 2) + gap, rcColor);
        }
    }
}