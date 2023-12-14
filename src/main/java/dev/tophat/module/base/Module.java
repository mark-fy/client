package dev.tophat.module.base;

import de.florianmichael.dietrichevents2.DietrichEvents2;

public class Module {

    private final String name = this.getClass().getAnnotation(ModuleInfo.class).name();
    private final Category category = this.getClass().getAnnotation(ModuleInfo.class).category();
    private int key = this.getClass().getAnnotation(ModuleInfo.class).bind();

    private boolean enabled, hidden;

    public void onEnable() {}
    public void onDisable() {}

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if(isEnabled()) {
            onEnable();
            DietrichEvents2.global().subscribe(this);
        } else {
            onDisable();
            DietrichEvents2.global().unsubscribe(this);
        }
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
