package tophat.fun.modules;

import org.lwjgl.Sys;
import tophat.fun.Client;
import tophat.fun.modules.settings.SettingInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.modules.settings.impl.NumberSetting;
import tophat.fun.modules.settings.impl.StringSetting;
import tophat.fun.utilities.Methods;

import java.lang.reflect.Field;

public class Module implements Methods {

    public String name = this.getClass().getAnnotation(ModuleInfo.class).name();
    public String desc = this.getClass().getAnnotation(ModuleInfo.class).desc();
    public Category category = this.getClass().getAnnotation(ModuleInfo.class).category();
    public int keyCode = this.getClass().getAnnotation(ModuleInfo.class).bind();

    private void registerSettings() {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(SettingInfo.class)) {
                SettingInfo settingInfo = field.getAnnotation(SettingInfo.class);

                try {
                    field.setAccessible(true);
                    Object value = field.get(this);

                    if (value instanceof BooleanSetting) {
                        BooleanSetting setting = (BooleanSetting) value;
                        setting.setName(settingInfo.name());
                        setting.setValue(settingInfo.booleanDefault());
                        Client.INSTANCE.settingManager.add(setting);
                    } else if (value instanceof StringSetting) {
                        StringSetting setting = (StringSetting) value;
                        setting.setName(settingInfo.name());
                        setting.setValue(settingInfo.stringDefault());
                        Client.INSTANCE.settingManager.add(setting);
                    } else if (value instanceof NumberSetting) {
                        NumberSetting setting = (NumberSetting) value;
                        setting.setName(settingInfo.name());
                        setting.setValue(settingInfo.intDefault());
                        Client.INSTANCE.settingManager.add(setting);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
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

        try {
            if (isEnabled()) {
                onEnable();
                Client.INSTANCE.eventManager.subscribe(this);
            } else {
                onDisable();
                Client.INSTANCE.eventManager.unsubscribe(this);
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

        COMBAT("Combat"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        RENDER("Render"),
        DESIGN("Design"),
        OTHERS("Others");

        private final String name;

        Category(String name) { this.name = name; }

        public String getName() { return name; }

    }

    public Category getCategory() { return category; }

}
