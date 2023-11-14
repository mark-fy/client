package wtf.tophat.client.modules.impl.hud;

import org.lwjgl.input.Keyboard;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.menus.click.astolfo.AstolfoClickGUI;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.menus.click.beta.BetaClickGUI;
import wtf.tophat.client.menus.click.material.MaterialClickGUI;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.StringSetting;

@ModuleInfo(name = "Click GUI",desc = "a clicky gui", category = Module.Category.HUD, bind = Keyboard.KEY_RSHIFT)
public class ClickGUI extends Module {

    private final StringSetting mode;

    public final BooleanSetting fontShadow, sound;

    public ClickGUI() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Style", "Concept", "Astolfo", "Material", "Concept"),
                fontShadow = new BooleanSetting(this, "Font Shadow", true).setHidden(() -> !mode.is("Dropdown")),
                sound = new BooleanSetting(this, "Sound", true)
        );
    }

    @Override
    public void onEnable() {
        switch (mode.get()) {
            case "Astolfo":
                mc.displayGuiScreen(new AstolfoClickGUI());
                break;
            case "Material":
                mc.displayGuiScreen(new MaterialClickGUI());
                break;
            case "Concept":
                mc.displayGuiScreen(new BetaClickGUI());
                break;
        }
        this.setEnabled(false);
        super.onEnable();
    }
}
