package tophat.fun.modules.impl.render;

import tophat.fun.Client;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.modules.settings.impl.StringSetting;

@ModuleInfo(name = "BlockAnimations", desc = "changes the sword block animation.", category = Module.Category.RENDER)
public class BlockAnimations extends Module {

    public final StringSetting blockStyle = new StringSetting(this, "BlockStyle", "1.7", "1.7", "Flux", "Swong");
    public final BooleanSetting blockHit = new BooleanSetting(this, "BlockHit", false);

    public BlockAnimations() {
        Client.INSTANCE.settingManager.add(
                blockStyle, blockHit
        );
    }

}
