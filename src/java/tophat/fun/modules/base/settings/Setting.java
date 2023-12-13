package tophat.fun.modules.base.settings;

import tophat.fun.modules.base.Module;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

public class Setting {

    protected String name;
    protected Module parent;

    private BooleanSupplier hidden = () -> false;

    public String getName() { return name; }

    public Module getParent() { return parent; }

    public boolean isHidden() { return hidden.getAsBoolean(); }

    public <T extends Setting> T setHidden(BooleanSupplier hidden) {
        this.hidden = hidden;
        return (T) this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Setting getSetting(String input, Module parent) {
        return getSettingsByModule(parent).stream().filter(setting -> getName().equalsIgnoreCase(input)).findFirst().get();
    }

    public static ArrayList<Setting> getSettingsByModule(Module module) {
        ArrayList<Setting> settingArrayList = new ArrayList<>();

        for (Setting setting : module.settings) {
            if (setting.getParent().equals(module)) {
                settingArrayList.add(setting);
            }
        }

        return settingArrayList.isEmpty() ? null : settingArrayList;
    }
}
