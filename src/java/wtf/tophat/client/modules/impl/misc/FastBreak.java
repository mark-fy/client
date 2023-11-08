package wtf.tophat.client.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.player.PlayerUtil;

@ModuleInfo(name = "Fast Break", desc = "break blocks faster", category = Module.Category.MISC)
public final class FastBreak extends Module {

    private final StringSetting mode;
    private final NumberSetting speed, ticks;

    public FastBreak(){
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Ticks", "Ticks", "Percentage"),
                speed = new NumberSetting(this, "Speed", 0, 100, 50, 0).setHidden(() -> !mode.is("Percentage")),
                ticks = new NumberSetting(this, "Ticks", 1, 100, 1, 0).setHidden(() -> !mode.is("Ticks"))
        );
    }

    @Listen
    public void onUpdate(UpdateEvent event){
        mc.playerController.blockHitDelay = 0;

        double percentageFaster = 0;

        switch (mode.get()) {
            case "Percentage":
                percentageFaster = speed.get().doubleValue() / 100f;
                break;

            case "Ticks":
                if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    BlockPos blockPos = mc.objectMouseOver.getBlockPos();
                    Block block = PlayerUtil.block(blockPos);

                    float blockHardness = block.getPlayerRelativeBlockHardness(mc.player, mc.world, blockPos);
                    percentageFaster = blockHardness * ticks.get().intValue();
                }
                break;
        }

        if (mc.playerController.curBlockDamageMP > 1 - percentageFaster) {
            mc.playerController.curBlockDamageMP = 1;
        }
    };
}