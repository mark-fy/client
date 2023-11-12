package wtf.tophat.client.utilities.render.animations;

import net.minecraft.client.renderer.GlStateManager;

public class AnimationUtil {
    public AnimationType animationType;
    public double value = 0;


    public AnimationUtil(AnimationType animationType) {
        this.animationType = animationType;
    }

    public void Render(double amount, double centerX, double centerY) {
        value = amount;
        GlStateManager.translate(centerX, centerY, 0);
        GlStateManager.scale(amount, amount, amount);
        GlStateManager.translate(-(centerX), -centerY, 0);
    }

    public enum AnimationType {
        SCALE,
        CUT,
    }

}