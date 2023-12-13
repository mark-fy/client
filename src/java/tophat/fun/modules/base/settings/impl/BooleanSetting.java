package tophat.fun.modules.base.settings.impl;

import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.settings.Setting;

public class BooleanSetting extends Setting {

    private boolean value;

    public BooleanSetting(Module parent, String name, boolean value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        parent.registerSettings(this);
    }

    public boolean toggle() {
        this.value = !this.value;

        return this.value;
    }

    public boolean get() { return value; }

    public void set(boolean value) {
        onChange(this.value, value);
        change(value);
    }

    public void change(boolean value) { this.value = value; }

    public void onChange(boolean old, boolean current) {}
}
