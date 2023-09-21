package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;

@ModuleInfo(name = "InvMove",desc = "move in guis", category = Module.Category.MOVE)
public class InvMove extends Module {

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            block3 : {
                KeyBinding[] moveKeys;
                block2 : {
                    moveKeys = new KeyBinding[]{mc.settings.keyBindForward, mc.settings.keyBindLeft, mc.settings.keyBindBack, mc.settings.keyBindRight, mc.settings.keyBindJump, mc.settings.keyBindSneak, mc.settings.keyBindSprint};
                    if (mc.currentScreen == null || (mc.currentScreen instanceof GuiChat))
                        break block2;
                    for (KeyBinding key : moveKeys) {
                        key.pressed = Keyboard.isKeyDown(key.getKeyCode());
                    }
                    break block3;
                }
                for (KeyBinding bind : moveKeys) {
                    if (Keyboard.isKeyDown(bind.getKeyCode()))
                        continue;
                    KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                }
            }
        }
    }
}
