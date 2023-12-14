package dev.tophat.mixin.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import dev.tophat.event.JumpListener;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(final EntityType<?> type, final World world) {
        super(type, world);
    }

    @Shadow
    public abstract float getYaw(final float tickDelta);

    @Redirect(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
    public float getJumpYaw(final LivingEntity instance) {
        if (instance instanceof ClientPlayerEntity) {
            final JumpListener.JumpEvent jumpEvent = new JumpListener.JumpEvent(instance.getYaw());
            DietrichEvents2.global().post(JumpListener.JumpEvent.ID, jumpEvent);

            return jumpEvent.getYaw();
        }
        return instance.getYaw();
    }
}
