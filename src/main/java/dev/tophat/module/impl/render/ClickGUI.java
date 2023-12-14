package dev.tophat.module.impl.render;

import dev.tophat.module.base.Category;
import dev.tophat.module.base.Module;
import dev.tophat.module.base.ModuleInfo;

@ModuleInfo(name = "ClickGUI", category = Category.RENDER, bind = 54)
public class ClickGUI extends Module {

    @Override
    public void onEnable() {
        System.out.println("hi");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        System.out.println("hi");
        super.onDisable();
    }
}
