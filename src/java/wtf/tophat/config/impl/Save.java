package wtf.tophat.config.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import wtf.tophat.Client;
import wtf.tophat.config.base.Config;
import wtf.tophat.config.base.ConfigInfo;
import wtf.tophat.modules.base.Module;
import wtf.tophat.settings.base.Setting;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.DividerSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ConfigInfo(name = "save", description = "save module state")
public class Save extends Config {

    public static String save(String name, String location) {
        Map<String, Map<String, Object>> modulesMap = new HashMap<>();

        for (Module.Category category : Module.Category.values()) {
            Map<String, Object> categoryModules = new HashMap<>();

            for (Module module : Client.moduleManager.getModulesByCategory(category)) {
                Map<String, Object> settingModules = new HashMap<>();

                for (Setting setting : Client.settingManager.getSettingsByModule(module)) {
                    if (setting instanceof DividerSetting) {
                        continue;
                    } else if (setting instanceof StringSetting) {
                        settingModules.put(setting.getName(), ((StringSetting) setting).get());
                    } else if (setting instanceof NumberSetting) {
                        settingModules.put(setting.getName(), ((NumberSetting) setting).get());
                    } else if (setting instanceof BooleanSetting) {
                        settingModules.put(setting.getName(), ((BooleanSetting) setting).get());
                    }
                }

                // Only add the module if it has settings
                if (!settingModules.isEmpty()) {
                    categoryModules.put(module.getName(), settingModules);
                }
            }

            // Only add the category if it has modules with settings
            if (!categoryModules.isEmpty()) {
                modulesMap.put(category.getName(), categoryModules);
            }
        }

        // Create a Gson instance with pretty printing
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(location + "/" + name + ".json")) {
            // Serialize the map to a JSON file
            gson.toJson(modulesMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to save the config.";
        }

        return String.format("Config saved as %s", name);
    }

}
