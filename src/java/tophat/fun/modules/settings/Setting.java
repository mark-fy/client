package tophat.fun.modules.settings;

import tophat.fun.modules.Module;

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

}
