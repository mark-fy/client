package wtf.tophat.client.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.util.EnumFacing;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.RayTraceRangeEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.Methods;

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
            if (Methods.mc.player.getHorizontalFacing() == EnumFacing.NORTH || Methods.mc.player.getHorizontalFacing() == EnumFacing.WEST) {
                correctedRange += n * 2.0f;
            }
        }
        event.setRange((float) correctedRange);
        event.setBlockReachDistance((float) Math.max(Methods.mc.playerController.getBlockReachDistance(), correctedRange));
    }
}
