package tophat.fun.utilities.player;

import net.minecraft.item.ItemSword;
import tophat.fun.utilities.Methods;

public class PlayerUtil implements Methods {

    public static boolean isHoldingSword() {
        return mc.thePlayer.ticksExisted > 3 && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

}
