package tophat.fun.modules.base.settings.impl;

import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.settings.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StringSetting extends Setting {

    private String value;
    private final List<String> all;

    public StringSetting(Module parent, String name, String value, String... all) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        this.all = new ArrayList<>(Arrays.asList(all));
        parent.registerSettings(this);
    }

    public List<String> getValues() { return all; }

    public String get() { return value; }

    public void set(String value) {
        onChange(this.value, value);
        change(value);
    }

    public void change(String value) { this.value = value; }

    public void forward() {
        int current = all.indexOf(value);
        int maximum = all.size();

        if (current < maximum - 1) {
            set(all.get(current + 1));
        } else {
            set(all.get(0));
        }

    }

    public void backward() {
        int current = all.indexOf(value);
        int maximum = all.size();

        if (current > 0) {
            set(all.get(current - 1));
        } else {
            set(all.get(maximum - 1));
        }
    }


    public boolean is(String input) { return value.equalsIgnoreCase(input); }

    public void onChange(String old, String current) {}
}
