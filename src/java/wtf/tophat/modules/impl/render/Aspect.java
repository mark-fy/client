package wtf.tophat.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.PerspectiveEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;

@ModuleInfo(name = "Aspect",desc = "simulate changing your aspect ratio", category = Module.Category.RENDER)
public class Aspect extends Module {

    private final NumberSetting aspect;
    private final BooleanSetting hands;

    public Aspect() {
        TopHat.settingManager.add(
                aspect = new NumberSetting(this, "Aspect", 0.1f, 5.0f, 1.0f, 1),
                hands = new BooleanSetting(this, "Hands", true)
        );
    }

    @Listen
    public void onAspect(PerspectiveEvent event) {
        if(!event.isHand() || hands.get()) {
            event.setAspect(aspect.get().floatValue());
        }
    }
}
