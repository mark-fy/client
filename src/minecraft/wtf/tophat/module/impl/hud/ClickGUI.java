package wtf.tophat.module.impl.hud;

import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;

@ModuleInfo(name = "Click GUI",desc = "clicky gui", category = Module.Category.HUD, bind = Keyboard.KEY_RSHIFT)
public class ClickGUI extends Module {

    public final BooleanSetting fontShadow;

    public ClickGUI() {
        Client.settingManager.add(
                fontShadow = new BooleanSetting(this, "Font Shadow", true)
        );
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new wtf.tophat.screen.cgui.ClickGUI());
        this.setEnabled(false);
        super.onEnable();
    }
}
