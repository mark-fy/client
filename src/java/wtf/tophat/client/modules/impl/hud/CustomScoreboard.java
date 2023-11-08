package wtf.tophat.client.modules.impl.hud;

import wtf.tophat.client.TopHat;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;

// Hooked in FontRenderer.java class
@ModuleInfo(name = "Custom Scoreboard",desc = "change the scoreboard", category = Module.Category.HUD)
public class CustomScoreboard extends Module {

    public final BooleanSetting customIp;

    public CustomScoreboard() {
        TopHat.settingManager.add(
                customIp = new BooleanSetting(this, "Custom IP", true)
        );
    }
}
