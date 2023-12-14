package dev.tophat.mixin.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import dev.tophat.event.OpenScreenListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void onSetScreen(final Screen screen, final CallbackInfo callbackInfo) {
        final OpenScreenListener.OpenScreenEvent openScreenEvent = new OpenScreenListener.OpenScreenEvent(screen);
        DietrichEvents2.global().post(OpenScreenListener.OpenScreenEvent.ID, openScreenEvent);

       if (openScreenEvent.isCancelled()) {
           callbackInfo.cancel();
       }
    }
}
