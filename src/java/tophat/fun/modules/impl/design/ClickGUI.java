package tophat.fun.modules.impl.design;

import org.lwjgl.input.Keyboard;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.modules.settings.impl.StringSetting;

@ModuleInfo(name = "ClickGUI", desc = "clicky gui.", category = Module.Category.DESIGN, bind = Keyboard.KEY_RSHIFT)
public class ClickGUI extends Module {

    private final StringSetting design = new StringSetting(this, "Design", "Dropdown", "Dropdown", "Material");

    public final BooleanSetting gradientOutline = new BooleanSetting(this, "GradientOutline", false);

    @Override
    public void onEnable() {
        switch (design.get()) {
            case "Dropdown":
                mc.displayGuiScreen(new tophat.fun.menu.clickgui.dropdown.ClickGUI());
                break;
            case "Material":
                mc.displayGuiScreen(new tophat.fun.menu.clickgui.material.ClickGUI());
                break;
        }

        this.setEnabled(false);
        super.onEnable();
    }

}
