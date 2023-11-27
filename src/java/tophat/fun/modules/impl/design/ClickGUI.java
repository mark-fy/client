package tophat.fun.modules.impl.design;

import org.lwjgl.input.Keyboard;
import tophat.fun.Client;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;

@ModuleInfo(name = "ClickGUI", desc = "clicky gui.", category = Module.Category.DESIGN, bind = Keyboard.KEY_RSHIFT)
public class ClickGUI extends Module {

    public final BooleanSetting gradientOutline = new BooleanSetting(this, "GradientOutline", false);

    public ClickGUI() {
        Client.INSTANCE.settingManager.add(
                gradientOutline
        );
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new tophat.fun.menu.clickgui.tophat.ClickGUI());
        this.setEnabled(false);
        super.onEnable();
    }

}
