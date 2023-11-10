package wtf.tophat.client.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.render.PerspectiveEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;

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
