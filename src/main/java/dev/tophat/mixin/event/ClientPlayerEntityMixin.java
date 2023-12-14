package dev.tophat.mixin.event;

import com.mojang.authlib.GameProfile;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import dev.tophat.event.PostUpdateListener;
import dev.tophat.event.PreUpdateListener;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(final ClientWorld world, final GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.BEFORE))
    public void onPreUpdate(final CallbackInfo callbackInfo) {
        DietrichEvents2.global().post(PreUpdateListener.PreUpdateEvent.ID, new PreUpdateListener.PreUpdateEvent());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onPostUpdate(final CallbackInfo callbackInfo) {
        DietrichEvents2.global().post(PostUpdateListener.PostUpdateEvent.ID, new PostUpdateListener.PostUpdateEvent());
    }
}
