package wtf.tophat.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.player.PlayerUtil;

@ModuleInfo(name = "Fast Break", desc = "break blocks faster", category = Module.Category.MISC)
public final class FastBreak extends Module {


   public final StringSetting mode = new StringSetting(this, "Mode", "Ticks", "Ticks", "Percentage");
   public final NumberSetting speed = new NumberSetting(this, "Speed", 0, 100, 50, 0).setHidden(() -> !mode.is("Percentage"));
   public final NumberSetting ticks = new NumberSetting(this, "Ticks", 1, 100, 1, 0).setHidden(() -> !mode.is("Ticks"));

   public FastBreak(){
       TopHat.settingManager.add(
               mode,
               speed,
               ticks
       );
   }

   @Listen
   public void onMotion(MotionEvent event){
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