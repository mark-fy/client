package tophat.fun.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.util.EnumFacing;
import tophat.fun.Client;
import tophat.fun.events.impl.player.ReachEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.modules.settings.impl.NumberSetting;

@ModuleInfo(name = "Reach", desc = "enables you to reach further.", category = Module.Category.COMBAT)
public class Reach extends Module {

    private final NumberSetting attackRange = new NumberSetting(this, "Attack Range", 3, 6, 3, 1);
    private final BooleanSetting fixMisplace = new BooleanSetting(this, "Fix Server-Side Misplace", true);

    double correctedRange;

    @Listen
    public void onReach(ReachEvent event) {
        correctedRange = attackRange.get().floatValue() + 0.00256f;
        if (fixMisplace.get()) {
            if (mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH || mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST) {
                correctedRange += 0.010625f * 2.0f;
            }
        }
        event.setRange((float) correctedRange);
        event.setBlockReachDistance((float) Math.max(mc.playerController.getBlockReachDistance(), correctedRange));
    }

}
