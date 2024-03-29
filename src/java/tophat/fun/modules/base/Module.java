package tophat.fun.modules.base;

import tophat.fun.Client;
import tophat.fun.modules.base.settings.Setting;
import tophat.fun.utilities.Methods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module implements Methods {

    public String name = this.getClass().getAnnotation(ModuleInfo.class).name();
    public String desc = this.getClass().getAnnotation(ModuleInfo.class).desc();
    public Category category = this.getClass().getAnnotation(ModuleInfo.class).category();
    public int keyCode = this.getClass().getAnnotation(ModuleInfo.class).bind();

    public List<Setting> settings = new ArrayList<Setting>();

    public void registerSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    public List<Setting> getSettings() {
        return settings;
    }

    private boolean enabled;
    private boolean hidden;

    public void onEnable() {}
    public void onDisable() {}

    public String getName() { return name; }

    public String getDesc() { return desc; }

    public int getKeyCode() { return keyCode; }

    public void setKeyCode(int keyCode) { this.keyCode = keyCode; }

    public boolean isEnabled() { return enabled; }

    public boolean isHidden() { return hidden; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (isEnabled()) {
            onEnable();
            Client.INSTANCE.eventManager.subscribe(this);
        } else {
            onDisable();
            Client.INSTANCE.eventManager.unsubscribe(this);
        }
    }

    public void setHidden(boolean hidden) { this.hidden = hidden; }

    public void toggle() { setEnabled(!isEnabled()); }

    public void renderIngame() {}

    public void renderDummy() {}

    public enum Category {

        COMBAT("Combat"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        RENDER("Render"),
        OTHERS("Others"),
        DESIGN("Design");

        private final String name;

        Category(String name) { this.name = name; }

        public String getName() { return name; }

    }

    public Category getCategory() { return category; }

}
