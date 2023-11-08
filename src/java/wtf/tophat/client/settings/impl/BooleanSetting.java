package wtf.tophat.client.settings.impl;

import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.settings.base.Setting;

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

}
