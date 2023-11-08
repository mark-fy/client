package wtf.tophat.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import wtf.tophat.TopHat;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.math.time.TimeUtil;

@ModuleInfo(name = "Eagle",desc = "sneak on edges", category = Module.Category.MOVE)
public class Eagle extends Module {

    private final NumberSetting delay;
    private final TimeUtil timer = new TimeUtil();

    public Eagle() {
        TopHat.settingManager.add(
                delay = new NumberSetting(this, "Sneak Delay", 0, 300, 30, 0)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            if (getWorld().getBlockState(new BlockPos(getX(), getY() - 1.0, getZ())).getBlock() instanceof BlockAir && getGround()) {
                if(timer.elapsed(delay.get().longValue(), true)) {
                    Methods.mc.settings.keyBindSneak.pressed = true;
                }
            } else {
                Methods.mc.settings.keyBindSneak.pressed = Keyboard.isKeyDown(Methods.mc.settings.keyBindSneak.getKeyCode());
            }
        }
    }

    @Override
    public void onDisable() {
        Methods.mc.settings.keyBindSneak.pressed = false;
        super.onDisable();
    }
}
