package wtf.tophat.module.impl.client;

import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.ColorSetting;
import wtf.tophat.settings.impl.NumberSetting;

import java.awt.*;

@ModuleInfo(name = "Theme",desc = "change the theme of the client", category = Module.Category.CLIENT)
public class Theme extends Module {

    public final NumberSetting red, green, blue;
    public final ColorSetting clientTheme;

    public Theme() {
        Client.settingManager.add(
                red = new NumberSetting(this, "Red", 0, 255, 0, 0),
                green = new NumberSetting(this, "Green", 0, 255, 85, 0),
                blue = new NumberSetting(this, "Blue", 0, 255, 255, 0),
                clientTheme = new ColorSetting(this, "Theme", new Color(0,85,255))
        );
    }

    @Override
    public void onEnable() {
        this.setEnabled(false);
        super.onEnable();
    }
}
