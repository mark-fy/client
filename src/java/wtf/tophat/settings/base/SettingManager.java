package wtf.tophat.settings.base;

import de.florianmichael.rclasses.storage.Storage;
import wtf.tophat.modules.base.Module;

import java.util.List;
import java.util.stream.Collectors;

public class SettingManager extends Storage<Setting> {

    @Override
    public void init() {}

    public Setting getSetting(String input, Module parent) {
        return getSettingsByModule(parent).stream().filter(setting -> setting.getName().equalsIgnoreCase(input)).findFirst().get();
    }

    public List<Setting> getSettingsByModule(Module parent) {
        return this.getList().stream().filter(setting -> setting.getParent() == parent).collect(Collectors.toList());
    }

}
