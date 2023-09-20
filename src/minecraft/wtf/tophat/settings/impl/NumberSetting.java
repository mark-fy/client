package wtf.tophat.settings.impl;

import wtf.tophat.module.base.Module;
import wtf.tophat.settings.base.Setting;

public class NumberSetting extends Setting {

    public Number min, max, value;
    public Integer decimalPoints;

    public NumberSetting(Module parent, String name, Number min, Number max, Number value, int decimalPoints) {
        this.parent = parent;
        this.name = name;
        this.min = min;
        this.max = max;
        this.value = value;
        this.decimalPoints = decimalPoints;
    }

    public Number getMinimum() { return min; }

    public Number getMaximum() { return max; }

    public Number getValue() { return value; }

    public void setValue(Number value) {
        change(value);
        onChange(this.value, value);
    }

    public void change(Number value) { this.value = value; }

    public void onChange(Number old, Number current) {}

    public double step() {
        if (min instanceof Double || max instanceof Double || value instanceof Double) {
            return 0.1;
        } else if (min instanceof Float || max instanceof Float || value instanceof Float) {
            return 0.01f;
        } else {
            return 1;
        }
    }

}
