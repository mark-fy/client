package dev.tophat.mixin.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import dev.tophat.event.KeyListener;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    public void onKey(final long window, final int key, final int scancode, final int action, final int modifiers, final CallbackInfo callbackInfo) {
        DietrichEvents2.global().post(KeyListener.KeyEvent.ID, new KeyListener.KeyEvent(key, scancode, action, modifiers));
    }
}
