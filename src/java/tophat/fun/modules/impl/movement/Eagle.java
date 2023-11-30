package tophat.fun.modules.impl.movement;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import tophat.fun.events.Event;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.NumberSetting;
import tophat.fun.utilities.Methods;
import tophat.fun.utilities.others.TimeUtil;

@ModuleInfo(name = "Eagle", desc = "sneak on edges.", category = Module.Category.MOVEMENT)
public class Eagle extends Module {

    private final NumberSetting delay = new NumberSetting(this, "Delay", 0, 300, 30);

    private final TimeUtil timer = new TimeUtil();

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            if (mc.theWorld.getBlockState(new BlockPos(getX(), getY() - 1.0, getZ())).getBlock() instanceof BlockAir && getGround()) {
                if(timer.elapsed(delay.get().longValue(), true)) {
                    Methods.mc.gameSettings.keyBindSneak.pressed = true;
                }
            } else {
                Methods.mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(Methods.mc.gameSettings.keyBindSneak.getKeyCode());
            }
        }
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        super.onDisable();
    }
}
