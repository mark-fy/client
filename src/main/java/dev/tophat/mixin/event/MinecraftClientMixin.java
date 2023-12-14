package dev.tophat.mixin.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import dev.tophat.event.TickListener;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(final CallbackInfo callbackInfo) {
        DietrichEvents2.global().post(TickListener.TickEvent.ID, new TickListener.TickEvent());
    }
}
