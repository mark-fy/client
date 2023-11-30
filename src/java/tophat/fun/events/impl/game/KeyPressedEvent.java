package tophat.fun.events.impl.game;

import net.minecraft.client.settings.KeyBinding;
import tophat.fun.events.Event;

public class KeyPressedEvent extends Event {

    public KeyBinding keyBind;
    public boolean pressed;

    public KeyPressedEvent(KeyBinding keyBind, boolean pressed) {
        this.keyBind = keyBind;
        this.pressed = pressed;
    }

    public KeyBinding getKeyBinding() {
        return keyBind;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}
