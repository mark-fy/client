package wtf.tophat.modules.impl.render;

import java.awt.Color;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;

@ModuleInfo(name = "Crosshair", desc = "changes your crosshair", category = Module.Category.RENDER)
public class Crosshair extends Module {

    public final BooleanSetting outline, renderTop, renderBottom, renderLeft, renderRight;
    public final NumberSetting pointedGap, dynamicGap, widthSetting, gapSetting, sizeSetting;

    public Crosshair(){
        TopHat.settingManager.add(
                outline = new BooleanSetting(this, "Outline", true),
                renderTop = new BooleanSetting(this, "Top", true),
                renderBottom = new BooleanSetting(this, "Bottom", true),
                renderLeft = new BooleanSetting(this, "Left", true),
                renderRight = new BooleanSetting(this, "Right", true),

                pointedGap = new NumberSetting(this, "Pointed Gap", 0.0, 15.0, 2.5, 1),
                dynamicGap = new NumberSetting(this, "Dynamic Gap", 0.0, 15.0, 2.5, 1),
                widthSetting = new NumberSetting(this, "Width", 0.25, 15.0, 1.0, 1),
                gapSetting = new NumberSetting(this, "Gap", 0.0, 15.0, 2.5, 1),
                sizeSetting = new NumberSetting(this, "Size", 0.25, 15.0, 2.5, 1)

        );
    }

    @Listen
    public void onRender2D(Render2DEvent e) {
        ScaledResolution sr = e.getScaledResolution();
        this.renderVerticalRects(sr);
        this.renderHorizontalRects(sr);
    };

    private void renderHorizontalRects(ScaledResolution sr) {
        float height = this.widthSetting.get().floatValue() / 2.0f;
        float gap = this.gapSetting.get().floatValue();
        float outlineSize = 0.5f;
        Color color1 = new Color(255, 255, 255);
        if (this.outline.get()) {
            if (this.renderLeft.get()) {
                Gui.drawRect3((float)(sr.getScaledWidth() / 2) - gap - this.sizeSetting.get().floatValue() - outlineSize, (float)(sr.getScaledHeight() / 2) - height - outlineSize, (float)(sr.getScaledWidth() / 2) - gap + outlineSize, (float)(sr.getScaledHeight() / 2) + height + outlineSize, Color.BLACK.getRGB());
            }
            if (this.renderRight.get()) {
                Gui.drawRect3((float)(sr.getScaledWidth() / 2) + gap - outlineSize, (float)(sr.getScaledHeight() / 2) - height - outlineSize, (float)(sr.getScaledWidth() / 2) + gap + this.sizeSetting.get().floatValue() + outlineSize, (float)(sr.getScaledHeight() / 2) + height + outlineSize, Color.BLACK.getRGB());
            }
        }
        if (this.renderLeft.get()) {
            Gui.drawRect3((float)(sr.getScaledWidth() / 2) - gap - this.sizeSetting.get().floatValue(), (float)(sr.getScaledHeight() / 2) - height, (float)(sr.getScaledWidth() / 2) - gap, (float)(sr.getScaledHeight() / 2) + height, color1.getRGB());
        }
        if (this.renderRight.get()) {
            Gui.drawRect3((float)(sr.getScaledWidth() / 2) + gap, (float)(sr.getScaledHeight() / 2) - height, (float)(sr.getScaledWidth() / 2) + gap + this.sizeSetting.get().floatValue(), (float)(sr.getScaledHeight() / 2) + height, color1.getRGB());
        }
    }

    private void renderVerticalRects(ScaledResolution sr) {
        float width = this.widthSetting.get().floatValue() / 2.0f;
        float gap = this.gapSetting.get().floatValue();
        float outlineSize = 0.5f;
        Color color1 = new Color(255, 255, 255);
        if (this.outline.get()) {
            if (this.renderTop.get()) {
                Gui.drawRect3((float)(sr.getScaledWidth() / 2) - width - outlineSize, (float)(sr.getScaledHeight() / 2) - gap - this.sizeSetting.get().floatValue() - outlineSize, (float)(sr.getScaledWidth() / 2) + width + outlineSize, (float)(sr.getScaledHeight() / 2) - gap + outlineSize, Color.BLACK.getRGB());
            }
            if (this.renderBottom.get()) {
                Gui.drawRect3((float)(sr.getScaledWidth() / 2) - width - outlineSize, (float)(sr.getScaledHeight() / 2) + gap + this.sizeSetting.get().floatValue() + outlineSize, (float)(sr.getScaledWidth() / 2) + width + outlineSize, (float)(sr.getScaledHeight() / 2) + gap - outlineSize, Color.BLACK.getRGB());
            }
        }
        if (this.renderTop.get()) {
            Gui.drawRect3((float)(sr.getScaledWidth() / 2) - width, (float)(sr.getScaledHeight() / 2) - gap - this.sizeSetting.get().floatValue(), (float)(sr.getScaledWidth() / 2) + width, (float)(sr.getScaledHeight() / 2) - gap, color1.getRGB());
        }
        if (this.renderBottom.get()) {
            Gui.drawRect3((float)(sr.getScaledWidth() / 2) - width, (float)(sr.getScaledHeight() / 2) + gap + this.sizeSetting.get().floatValue(), (float)(sr.getScaledWidth() / 2) + width, (float)(sr.getScaledHeight() / 2) + gap, color1.getRGB());
        }
    }
}