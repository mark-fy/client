package wtf.tophat.utilities.player;

import net.minecraft.entity.EntityLivingBase;
import wtf.tophat.utilities.Methods;

public class PlayerUtil implements Methods {

    public static boolean isMathGround() {
        return mc.player.posY % 0.015625 == 0;
    }

    public static boolean isOnSameTeam(EntityLivingBase entity) {
        if (entity.getTeam() != null && mc.player.getTeam() != null) {
            char c1 = entity.getDisplayName().getFormattedText().charAt(1);
            char c2 = mc.player.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        } else {
            return false;
        }
    }

}
