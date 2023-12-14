package dev.tophat.module.impl.combat;

import dev.tophat.module.base.Category;
import dev.tophat.module.base.Module;
import dev.tophat.module.base.ModuleInfo;

@ModuleInfo(name = "Aura", category = Category.COMBAT)
public class Aura extends Module {

    @Override
    public void onEnable() {
        System.out.println("aura on");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        System.out.println("aura off");
        super.onDisable();
    }

}
