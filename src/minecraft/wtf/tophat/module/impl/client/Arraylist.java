package wtf.tophat.module.impl.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.base.Setting;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.ModeSetting;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static wtf.tophat.utilities.ColorPallete.*;

@ModuleInfo(name = "Arraylist",desc = "lists the enabled modules", category = Module.Category.CLIENT)
public class Arraylist extends Module {

    private final BooleanSetting hideVisualModules, fontShadow, suffix;

    public Arraylist() {
        Client.settingManager.add(
                hideVisualModules = new BooleanSetting(this, "Hide Visual Modules", true),
                fontShadow = new BooleanSetting(this, "Font Shadow", true),
                suffix = new BooleanSetting(this, "Suffix", true)
        );
        setEnabled(true);
    }

    private long lastColorChange = System.currentTimeMillis();
    private int colorChangeInterval = 2000;

    public void renderIngame() {
        ScaledResolution sr = new ScaledResolution(mc);
        CFontRenderer fr = CFontUtil.SF_Semibold_20.getRenderer();

        List<Module> enabledModules = Client.moduleManager.getEnabledModules()
                .stream()
                .filter(module ->
                                (!hideVisualModules.get() ||
                                (!module.getCategory().equals(Category.RENDER) &&
                                !module.getCategory().equals(Category.CLIENT)))
                )
                .sorted((module1, module2) -> {
                    int width1 = calculateTotalWidth(module1, fr);
                    int width2 = calculateTotalWidth(module2, fr);
                    return width2 - width1;
                })
                .collect(Collectors.toList());

        if (enabledModules.isEmpty()) {
            return;
        }

        int y = 6;
        int maxWidth = enabledModules.stream()
                .mapToInt(module -> calculateTotalWidth(module, fr) + 2)
                .max()
                .orElse(0);

        int counter = 0;
        DrawingUtil.rectangle(sr.getScaledWidth() - maxWidth - 5, y - 1, maxWidth + 2, 1, true, new Color(ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L)));
        for (Module module : enabledModules) {
            String moduleName = module.getName().toLowerCase(Locale.ROOT);
            String modeText = "";

            for (Setting setting : Client.settingManager.getSettingsByModule(module)) {
                if (setting instanceof ModeSetting) {
                    if (suffix.get()) {
                        modeText = EnumChatFormatting.WHITE + " [" + ((ModeSetting) setting).get() + "]".toLowerCase(Locale.ROOT);
                    }
                    break;
                }
            }

            String fullText = moduleName + modeText;

            DrawingUtil.rectangle(sr.getScaledWidth() - fr.getStringWidth(fullText) - 6 - 2, y, fr.getStringWidth(fullText) + 5, fr.getHeight() + 3, true, new Color(0, 0, 0, 128));

            fr.drawStringChoose(fontShadow.get(), fullText, sr.getScaledWidth() - 7 - fr.getStringWidth(fullText), y + (fr.getHeight() + 2 - fr.getHeight()) / 2, new Color(ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L)));

            DrawingUtil.rectangle(sr.getScaledWidth() - maxWidth - 4 + maxWidth, y, 1, fr.getHeight() + 3, true, new Color(ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L)));

            y += 11;
            counter++;
        }
    }

    private int calculateTotalWidth(Module module, CFontRenderer fr) {
        String moduleName = module.getName().toLowerCase(Locale.ROOT);
        String modeText = "";

        if(suffix.get()) {
            for (Setting setting : Client.settingManager.getSettingsByModule(module)) {
                if (setting instanceof ModeSetting) {
                    modeText = " [" + ((ModeSetting) setting).get() + "]";
                    break;
                }
            }
        }

        String fullText = moduleName + modeText;
        return fr.getStringWidth(fullText);
    }

    @Override
    public void renderDummy() { renderIngame(); }
}
