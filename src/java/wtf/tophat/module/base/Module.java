package wtf.tophat.module.base;

import wtf.tophat.Client;
import wtf.tophat.utilities.Methods;

public class Module implements Methods {

    public String name = this.getClass().getAnnotation(ModuleInfo.class).name();
    public String desc = this.getClass().getAnnotation(ModuleInfo.class).desc();
    public Category category = this.getClass().getAnnotation(ModuleInfo.class).category();
    public int keyCode = this.getClass().getAnnotation(ModuleInfo.class).bind();

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

        if(isEnabled()) {
            if(mc.world != null || mc.player != null) {
                onEnable();
                Client.eventManager.subscribe(this);
            }
        } else {
            if(mc.world != null || mc.player != null) {
                onDisable();
                Client.eventManager.unsubscribe(this);
            }
        }
    }

    public void setHidden(boolean hidden) { this.hidden = hidden; }

    public void toggle() { setEnabled(!isEnabled()); }

    public void renderIngame() {}

    public void renderDummy() {}

    public enum Category {

        COMBAT ("Combat"),
        MOVE ("Move"),
        PLAYER ("Player"),
        EXPLOIT("Exploit"),
        RENDER ("Render"),
        HUD("Hud");

        private final String name;

        Category(String name) { this.name = name; }

        public String getName() { return name; }

    }

    public Category getCategory() { return category; }

}
