package wtf.tophat.utilities.render;

import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.modules.base.Module;
import wtf.tophat.utilities.Methods;

import java.awt.*;
import java.util.Arrays;
import java.util.Locale;

public class CategoryUtil implements Methods {

    public static String getCategoryLetter(Module.Category category) {
        if(category.equals(Module.Category.COMBAT)) {
            return "a";
        } else if(category.equals(Module.Category.MOVE)) {
            return "c";
        } else if(category.equals(Module.Category.RENDER)) {
            return "f";
        } else if(category.equals(Module.Category.PLAYER)) {
            return "e";
        } else if(category.equals(Module.Category.HUD)) {
            return "d";
        } else
            return "b";
    }

    public static int getCategoryColor(Module.Category category) {
        if(category == Module.Category.COMBAT) {
            return new Color(230, 77, 62).getRGB();
        } else if(category == Module.Category.MOVE) {
            return new Color(48, 203, 116).getRGB();
        } else if(category == Module.Category.RENDER) {
            return new Color(245, 155, 27).getRGB();
        } else if(category == Module.Category.PLAYER) {
            return new Color(141, 67, 169).getRGB();
        } else if(category == Module.Category.HUD) {
            return new Color(56, 0, 196).getRGB();
        } else
            return new Color(75, 145, 190).getRGB();
    }

    public static Color getCategoryColor1(Module.Category category) {
        if(category == Module.Category.COMBAT) {
            return new Color(230, 77, 62);
        } else if(category == Module.Category.MOVE) {
            return new Color(48, 203, 116);
        } else if(category == Module.Category.RENDER) {
            return new Color(245, 155, 27);
        } else if(category == Module.Category.PLAYER) {
            return new Color(141, 67, 169);
        } else if(category == Module.Category.HUD) {
            return new Color(56, 0, 196);
        } else
            return new Color(75, 145, 190);
    }

    public static double getTotalCategoryWidth(Module input) {
        double totalCategoryWidth = 0;

        for (Module.Category category : Module.Category.values()) {
            double categoryWidth = getMaxModuleNameWidth(category, input) + 20;
            totalCategoryWidth += categoryWidth;
        }

        return totalCategoryWidth;
    }

    public static double getMaxModuleNameWidth(Module.Category category, Module listeningModule) {
        FontRenderer fr = mc.fontRenderer;
        double maxModuleNameWidth = 0;

        for (Module module : Client.moduleManager.getModulesByCategory(category)) {
            String keybindText;
            int keybindTextWidth;

            if (module == listeningModule) {
                keybindText = " [...]";
                keybindTextWidth = fr.getStringWidth(keybindText);
            } else if (module.getKeyCode() == Keyboard.KEY_NONE) {
                keybindText = " [NONE]";
                keybindTextWidth = fr.getStringWidth(keybindText);
            } else {
                keybindText = " [" + Keyboard.getKeyName(module.getKeyCode()) + "]";
                keybindTextWidth = fr.getStringWidth(keybindText);
            }

            double moduleNameWidth = fr.getStringWidth(module.getName().toLowerCase(Locale.ROOT)) + keybindTextWidth;
            if (moduleNameWidth > maxModuleNameWidth) {
                maxModuleNameWidth = moduleNameWidth;
            }
        }

        return maxModuleNameWidth;
    }

    public static int getCategoryY(Module.Category category) {
        int index = Arrays.asList(Module.Category.values()).indexOf(category);
        return 20 + index * 12;
    }

}
