package dev.tophat.mixin.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import dev.tophat.event.Render2DListener;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    public void onRender2D(final DrawContext context, final float tickDelta, final CallbackInfo callbackInfo) {
        DietrichEvents2.global().post(Render2DListener.Render2DEvent.ID, new Render2DListener.Render2DEvent(context, tickDelta));
    }

}
