package wtf.tophat.settings.impl;

import wtf.tophat.module.base.Module;
import wtf.tophat.settings.base.Setting;

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

    public boolean getValue() { return value; }

    public void setValue(boolean value) {
        onChange(this.value, value);
        change(value);
    }

    public void change(boolean value) { this.value = value; }

    public void onChange(boolean old, boolean current) {}

}
