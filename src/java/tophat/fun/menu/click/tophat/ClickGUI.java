package tophat.fun.menu.click.tophat;

import net.minecraft.client.gui.GuiScreen;
import tophat.fun.Client;
import tophat.fun.modules.Module;
import tophat.fun.modules.settings.Setting;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.modules.settings.impl.StringSetting;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.render.RectUtil;
import tophat.fun.utilities.render.RoundUtil;
import tophat.fun.utilities.render.TextUtil;

import java.awt.*;
import java.io.IOException;

public class ClickGUI extends GuiScreen {

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsMedium 18");
    private final static TTFFontRenderer poppinsR = CFont.FONT_MANAGER.getFont("PoppinsRegular 18");
    private final static TTFFontRenderer iconFont = CFont.FONT_MANAGER.getFont("RegularIcons 18");

    private Module expandedModule = null;

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float x = 60;
        float y = 5;
        float modHeight = 20;

        float catOffset = 100;
        for (Module.Category category : Module.Category.values()) {
            // background -- expands when needed
            RoundUtil.drawRoundedRect(x + catOffset - 1, y - 1, 102, 22 + Client.INSTANCE.moduleManager.getModulesByCategory(category).size() * modHeight, 8, new Color(24, 175, 162));
            RoundUtil.drawRoundedRect(x + catOffset, y, 100, 20 + Client.INSTANCE.moduleManager.getModulesByCategory(category).size() * modHeight, 8, new Color(19, 19, 19));
            RoundUtil.drawRoundedRect(x + catOffset, y, 100, 20, 8, new Color(25, 25, 25));
            poppins.drawString(category.getName(), x + catOffset + 5, y + 5, -1);
            iconFont.drawString(TextUtil.getCategoryLetter(category), x + catOffset + 85, y + 7, -1);

            float modOffset = y + 4 + 20;
            for (Module module : Client.INSTANCE.moduleManager.getModulesByCategory(category)) {
                if (module.isHidden()) continue;
                poppinsR.drawString(module.getName(), x + catOffset + 5, modOffset, module.isEnabled() ? new Color(37, 239, 223).getRGB() : Color.WHITE.getRGB());

                if (expandedModule == module) {
                    // divider/spacer
                    RectUtil.rectangle(x + catOffset + 5, modOffset + 13, 90, 1, true, new Color(35, 35, 35));

                    float settOffset = modOffset;
                    for (Setting setting : Client.INSTANCE.settingManager.getSettingsByModule(expandedModule)) {
                        if (setting.isHidden()) continue;

                        if (setting instanceof StringSetting) {
                            poppinsR.drawString(setting.getName() + ": " + ((StringSetting) setting).get(), x + catOffset + 5, settOffset + 20, -1);
                            modHeight += 20;
                            settOffset += 20;
                        } else if (setting instanceof BooleanSetting) {
                            poppinsR.drawString(setting.getName(), x + catOffset + 5, settOffset + 20, -1);
                            RoundUtil.drawRoundedRect(x + catOffset + 83, settOffset + 19, 12, 12, 6, new Color(24, 175, 162));
                            RoundUtil.drawRoundedRect(x + catOffset + 84, settOffset + 20, 10, 10, 4, ((BooleanSetting) setting).get() ? new Color(24, 175, 162) : new Color(25, 25, 25));
                            poppinsR.drawString("✓", x + catOffset + 86, settOffset + 20, Color.WHITE.getRGB());
                            fontRendererObj.drawStringWithShadow(((BooleanSetting) setting).get() ? "✓" : "", x + catOffset + 86, settOffset + 20, -1);
                            modHeight += 20;
                            settOffset += 20;
                        }
                    }
                }

                modOffset += 20;
            }

            catOffset += 120;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        float x = 60;
        float y = 5;

        float catOffset = 100;
        for (Module.Category category : Module.Category.values()) {
            float modOffset = y + 4 + 20;
            for(Module module : Client.INSTANCE.moduleManager.getModulesByCategory(category)) {
                if(module.isHidden()) continue;
                boolean hover = RectUtil.hovered(mouseX, mouseY, x + catOffset, modOffset, 100, 18);

                if(hover && mouseButton == 0) {
                    module.toggle();
                } else if(hover && mouseButton == 1) {
                    expandedModule = (expandedModule == module) ? null : module;
                }
                modOffset += 20;
            }
            catOffset += 120;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
