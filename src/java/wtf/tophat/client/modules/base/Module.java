package wtf.tophat.client.modules.base;

import wtf.tophat.client.TopHat;
import wtf.tophat.client.utilities.Methods;
import wtf.tophat.client.utilities.sound.SoundUtil;

public class Module implements Methods {

    public String name = this.getClass().getAnnotation(ModuleInfo.class).name();
    public String desc = this.getClass().getAnnotation(ModuleInfo.class).desc();
    public Category category = this.getClass().getAnnotation(ModuleInfo.class).category();
    public int keyCode = this.getClass().getAnnotation(ModuleInfo.class).bind();

    private boolean enabled;
    private boolean hidden;

    public void onEnable() {
        SoundUtil.play(SoundUtil.toggleOnSound);
    }
    public void onDisable() {
        SoundUtil.play(SoundUtil.toggleOffSound);
    }

    public String getName() { return name; }

    public String getDesc() { return desc; }

    public int getKeyCode() { return keyCode; }

    public void setKeyCode(int keyCode) { this.keyCode = keyCode; }

    public boolean isEnabled() { return enabled; }

    public boolean isHidden() { return hidden; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        try {
            if (isEnabled()) {
                onEnable();
                TopHat.eventManager.subscribe(this);
            } else {
                onDisable();
                TopHat.eventManager.unsubscribe(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        MISC("Misc"),
        RENDER ("Render"),
        HUD("Hud");

        private final String name;

        Category(String name) { this.name = name; }

        public String getName() { return name; }

    }

    public Category getCategory() { return category; }

}
