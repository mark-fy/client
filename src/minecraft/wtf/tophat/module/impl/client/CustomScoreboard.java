package wtf.tophat.module.impl.client;

import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;

// Hooked in FontRenderer.java class
@ModuleInfo(name = "CustomScoreboard",desc = "change the scoreboard", category = Module.Category.CLIENT)
public class CustomScoreboard extends Module {

    public final BooleanSetting customIp;

    public CustomScoreboard() {
        Client.settingManager.add(
                customIp = new BooleanSetting(this, "Custom IP", true)
        );
    }
}
