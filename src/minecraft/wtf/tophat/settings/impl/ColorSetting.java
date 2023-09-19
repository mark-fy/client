package wtf.tophat.settings.impl;

import wtf.tophat.module.base.Module;
import wtf.tophat.settings.base.Setting;

import java.awt.*;

public class ColorSetting extends Setting {

    private Color value;

    public ColorSetting(Module parent, String name, Color value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
    }

    public Color get() { return value; }

    public void set(Color value) {
        onChange(this.value, value);
        change(value);
    }

    public void change(Color value) { this.value = value; }

    public void onChange(Color old, Color current) {}

}
