package tophat.fun.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import tophat.fun.events.impl.render.PerspectiveEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.BooleanSetting;
import tophat.fun.modules.base.settings.impl.NumberSetting;

@ModuleInfo(name = "Aspect", desc = "modify your view aspect.", category = Module.Category.RENDER)
public class Aspect extends Module {

    private final NumberSetting aspect = new NumberSetting(this, "Aspect", 0.1f, 5.0f, 1.0f, 1);
    private final BooleanSetting hands = new BooleanSetting(this, "Hands", true);

    @Listen
    public void onAspect(PerspectiveEvent event) {
        if(!event.isHand() || hands.get()) {
            event.setAspect(aspect.get().floatValue());
        }
    }

}
