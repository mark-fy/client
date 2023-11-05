package wtf.tophat.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.KeyBinding;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;

@ModuleInfo(name = "Sneak",desc = "sneak automatically", category = Module.Category.MOVE)
public class Sneak extends Module {

    private final BooleanSetting legit;

    public Sneak() {
        TopHat.settingManager.add(
                legit = new BooleanSetting(this, "Legit", true)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(legit.get())
            mc.settings.keyBindSneak.pressed = true;
        else
            mc.player.setSneaking(true);
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.settings.keyBindSneak.getKeyCode(), false);
        super.onDisable();
    }
}
