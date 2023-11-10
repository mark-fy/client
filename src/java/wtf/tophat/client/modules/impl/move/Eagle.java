package wtf.tophat.client.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.Methods;
import wtf.tophat.client.utilities.math.time.TimeUtil;

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
