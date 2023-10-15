package wtf.tophat.module.impl.hud;

import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.screen.click.beta.BetaClickGUI;
import wtf.tophat.screen.click.dropdown.DropDownClickGUI;
import wtf.tophat.screen.click.material.MaterialClickGUI;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;

@ModuleInfo(name = "Click GUI",desc = "a clicky gui", category = Module.Category.HUD, bind = Keyboard.KEY_RSHIFT)
public class ClickGUI extends Module {

    private final StringSetting mode;

    public final BooleanSetting fontShadow;

    public ClickGUI() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Style", "Dropdown", "Dropdown", "Material", "Beta"),
                fontShadow = new BooleanSetting(this, "Font Shadow", true)
        );
    }

    @Override
    public void onEnable() {
        switch (mode.getValue()) {
            case "Dropdown":
                mc.displayGuiScreen(new DropDownClickGUI());
                break;
            case "Material":
                mc.displayGuiScreen(new MaterialClickGUI());
                break;
            case "Beta":
                mc.displayGuiScreen(new BetaClickGUI());
                break;
        }
        this.setEnabled(false);
        super.onEnable();
    }
}
