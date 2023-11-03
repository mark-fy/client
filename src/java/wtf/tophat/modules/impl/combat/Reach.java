package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.util.EnumFacing;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.RayTraceRangeEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;

@ModuleInfo(name = "Reach", desc = "enables you to hit further", category = Module.Category.COMBAT)
public class Reach extends Module {

    private final NumberSetting attackRange;
    private final BooleanSetting fixMisplace;

    public Reach() {
        TopHat.settingManager.add(
                attackRange = new NumberSetting(this, "Attack Range", 3, 6, 3, 1),
                fixMisplace = new BooleanSetting(this, "Fix Server-Side Misplace", true)
        );
    }

    double correctedRange;

    @Listen
    public void onReach(RayTraceRangeEvent event) {
        correctedRange = attackRange.get().floatValue() + 0.00256f;
        if (fixMisplace.get()) {
            final float n = 0.010625f;
            if (mc.player.getHorizontalFacing() == EnumFacing.NORTH || mc.player.getHorizontalFacing() == EnumFacing.WEST) {
                correctedRange += n * 2.0f;
            }
        }
        event.setRange((float) correctedRange);
        event.setBlockReachDistance((float) Math.max(mc.playerController.getBlockReachDistance(), correctedRange));
    }
}
