package wtf.tophat.modules.impl.hud;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import wtf.tophat.Client;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.base.Setting;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import static wtf.tophat.utilities.render.Colors.*;

@SuppressWarnings("ConstantValue")
@ModuleInfo(name = "Arraylist",desc = "lists the enabled modules", category = Module.Category.HUD)
public class Arraylist extends Module {

    private final StringSetting color, suffixMode, barMode;
    private final BooleanSetting hideVisualModules, suffix, bar;

    public Arraylist() {
        Client.settingManager.add(
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Astolfo", "Rainbow", "Brown"),
                suffix = new BooleanSetting(this, "Suffix", true),
                suffixMode = new StringSetting(this, "Suffix Type", "n [s]", "n [s]", "n (s)", "n - s", "n s", "n > s", "n $ s", "n % s", "n # s", "n | s", "n -> s", "n » s")
                        .setHidden(() -> !suffix.get()),
                bar = new BooleanSetting(this, "Bar", true),
                barMode = new StringSetting(this, "Bar Type", "both", "top", "right", "both")
                        .setHidden(() -> !bar.get()),
                hideVisualModules = new BooleanSetting(this, "Hide Visual Modules", true)
        );
        setEnabled(true);
    }

    public void renderIngame() {
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRenderer;

        List<Module> enabledModules = Client.moduleManager.getEnabledModules()
                .stream()
                .filter(module ->
                        (!hideVisualModules.get() ||
                                (!module.getCategory().equals(Category.RENDER) &&
                                        !module.getCategory().equals(Category.HUD)))
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
        int rcColor = 0;

        switch (this.color.get()) {
            case "Gradient":
                rcColor = ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L);
                break;
            case "Rainbow":
                rcColor = ColorUtil.getRainbow(3000, (int) (counter * 150L));
                break;
            case "Astolfo":
                rcColor = ColorUtil.blendRainbowColours(counter * 150L);
                break;
            case "Brown":
                rcColor = ColorUtil.fadeBetween(GORGE_COLOR, LIGHT_GORGE_COLOR, counter * 150L);
                break;
        }

        DrawingUtil.rectangle(sr.getScaledWidth() - maxWidth - 5, y - 1, maxWidth + 2, 1, true, new Color(rcColor));
        for (Module module : enabledModules) {
            String moduleName = module.getName();
            String modeText = "";

            int color = 0;

            switch (this.color.get()) {
                case "Gradient":
                    color = ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L);
                    break;
                case "Rainbow":
                    color = ColorUtil.getRainbow(3000, (int) (counter * 150L));
                    break;
                case "Astolfo":
                    color = ColorUtil.blendRainbowColours(counter * 150L);
                    break;
                case "Brown":
                    color = ColorUtil.fadeBetween(GORGE_COLOR, LIGHT_GORGE_COLOR, counter * 150L);
                    break;
            }

            for (Setting setting : Client.settingManager.getSettingsByModule(module)) {
                if (setting instanceof StringSetting) {
                    if (suffix.get()) {
                        switch (suffixMode.get()) {
                            case "n [s]":
                                modeText = EnumChatFormatting.WHITE + " [" + ((StringSetting) setting).get() + "]";
                                break;
                            case "n (s)":
                                modeText = EnumChatFormatting.WHITE + " (" + ((StringSetting) setting).get() + ")";
                                break;
                            case "n - s":
                                modeText = EnumChatFormatting.WHITE + " - " + ((StringSetting) setting).get();
                                break;
                            case "n s":
                                modeText = EnumChatFormatting.WHITE + " " + ((StringSetting) setting).get();
                                break;
                            case "n > s":
                                modeText = EnumChatFormatting.WHITE + " > " + ((StringSetting) setting).get();
                                break;
                            case "n $ s":
                                modeText = EnumChatFormatting.WHITE + " $ " + ((StringSetting) setting).get();
                                break;
                            case "n % s":
                                modeText = EnumChatFormatting.WHITE + " % " + ((StringSetting) setting).get();
                                break;
                            case "n # s":
                                modeText = EnumChatFormatting.WHITE + " # " + ((StringSetting) setting).get();
                                break;
                            case "n | s":
                                modeText = EnumChatFormatting.WHITE + " | " + ((StringSetting) setting).get();
                                break;
                            case "n -> s":
                                modeText = EnumChatFormatting.WHITE + " -> " + ((StringSetting) setting).get();
                                break;
                            case "n » s":
                                modeText = EnumChatFormatting.WHITE + " » " + ((StringSetting) setting).get();
                                break;
                        }
                    }
                    break;
                }
            }

            String fullText = moduleName + modeText;
            DrawingUtil.rectangle(sr.getScaledWidth() - fr.getStringWidth(fullText) - 6 - 2, y, fr.getStringWidth(fullText) + 5, fr.FONT_HEIGHT + 2, true, new Color(0, 0, 0, 128));
            fr.drawString(fullText, sr.getScaledWidth() - 7 - fr.getStringWidth(fullText), y + (fr.FONT_HEIGHT + 2 - fr.FONT_HEIGHT) / 2 + 1, new Color(color).getRGB());
            DrawingUtil.rectangle(sr.getScaledWidth() - maxWidth - 4 + maxWidth, y, 1, fr.FONT_HEIGHT + 2, true, new Color(color));

            y += 11;
            counter++;
        }
    }

    private int calculateTotalWidth(Module module, FontRenderer fr) {
        String moduleName = module.getName();
        String modeText = "";

        if (suffix.get()) {
            for (Setting setting : Client.settingManager.getSettingsByModule(module)) {
                if (setting instanceof StringSetting) {
                    switch (suffixMode.get()) {
                        case "n [s]":
                            modeText = EnumChatFormatting.WHITE + " [" + ((StringSetting) setting).get() + "]";
                            break;
                        case "n (s)":
                            modeText = EnumChatFormatting.WHITE + " (" + ((StringSetting) setting).get() + ")";
                            break;
                        case "n - s":
                            modeText = EnumChatFormatting.WHITE + " - " + ((StringSetting) setting).get();
                            break;
                        case "n s":
                            modeText = EnumChatFormatting.WHITE + " " + ((StringSetting) setting).get();
                            break;
                        case "n > s":
                            modeText = EnumChatFormatting.WHITE + " > " + ((StringSetting) setting).get();
                            break;
                        case "n $ s":
                            modeText = EnumChatFormatting.WHITE + " $ " + ((StringSetting) setting).get();
                            break;
                        case "n % s":
                            modeText = EnumChatFormatting.WHITE + " % " + ((StringSetting) setting).get();
                            break;
                        case "n # s":
                            modeText = EnumChatFormatting.WHITE + " # " + ((StringSetting) setting).get();
                            break;
                        case "n | s":
                            modeText = EnumChatFormatting.WHITE + " | " + ((StringSetting) setting).get();
                            break;
                        case "n -> s":
                            modeText = EnumChatFormatting.WHITE + " -> " + ((StringSetting) setting).get();
                            break;
                        case "n » s":
                            modeText = EnumChatFormatting.WHITE + " » " + ((StringSetting) setting).get();
                            break;
                    }
                    break;
                }
            }
        }

        String fullText = moduleName + modeText;
        return fr.getStringWidth(fullText) + 1;
    }

    @Override
    public void renderDummy() { renderIngame(); }
}
