package tophat.fun.modules.impl.design;

import org.lwjgl.input.Keyboard;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;

@ModuleInfo(name = "ClickGUI", desc = "clicky gui", category = Module.Category.DESIGN, bind = Keyboard.KEY_RSHIFT)
public class ClickGUI extends Module {

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new tophat.fun.menu.click.tophat.ClickGUI());
        this.setEnabled(false);
        super.onEnable();
    }

}
