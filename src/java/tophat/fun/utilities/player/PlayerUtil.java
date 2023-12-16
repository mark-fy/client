package tophat.fun.utilities.player;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemSword;
import tophat.fun.utilities.Methods;

public class PlayerUtil implements Methods {

    public static boolean sprintReset;

    public static double getRange(Entity entity) {
        if (mc.thePlayer == null)
            return 0;

        return mc.thePlayer.getPositionEyes(1.0f).distanceTo(RotationUtil.getBestVector(mc.thePlayer.getPositionEyes(1F),
                entity.getEntityBoundingBox()));
    }

    public static boolean isHoldingSword() {
        return mc.thePlayer.ticksExisted > 3 && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

}
