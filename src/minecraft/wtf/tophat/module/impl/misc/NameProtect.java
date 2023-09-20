package wtf.tophat.module.impl.misc;

import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.utilities.chat.ChatUtil;

// Hooked in FontRenderer.java class
@ModuleInfo(name = "NameProtect",desc = "hides your name client side", category = Module.Category.MISC)
public class NameProtect extends Module {

    private final BooleanSetting lol;

    public NameProtect() {
        Client.settingManager.add(
                lol = new BooleanSetting(this, "Lol", false)
        );
    }

    @Override
    public void onEnable() {
        ChatUtil.addChatMessage("lol");
        ChatUtil.addChatMessage(lol.getName());
        super.onEnable();
    }
}
