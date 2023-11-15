package tophat.fun.modules.settings.impl;

import tophat.fun.modules.Module;
import tophat.fun.modules.settings.Setting;

public class BooleanSetting extends Setting {

    private boolean value;

    public BooleanSetting(Module parent, String name, boolean value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
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

    public void setValue(boolean value) {
        this.value = value;
    }
}
