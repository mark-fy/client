package wtf.tophat.settings.impl;

import wtf.tophat.module.base.Module;
import wtf.tophat.settings.base.Setting;

public class DividerSetting extends Setting {

    public DividerSetting(Module parent, String name) {
        this.parent = parent;
        this.name = name;
    }
}
