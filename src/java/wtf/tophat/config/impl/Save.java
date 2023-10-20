package wtf.tophat.config.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import wtf.tophat.Client;
import wtf.tophat.config.base.Config;
import wtf.tophat.config.base.ConfigInfo;
import wtf.tophat.modules.base.Module;
import wtf.tophat.settings.base.Setting;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.DividerSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.Methods;

import java.io.FileWriter;
import java.io.IOException;

@ConfigInfo(name = "save", description = "save module state")
public class Save extends Config {

    public static String save(String name, String location) {
        JsonObject rootObject = new JsonObject();

        // Add Client information
        JsonObject clientInfo = new JsonObject();
        clientInfo.addProperty("Version", Client.getVersion());
        clientInfo.addProperty("Date", Methods.getCurrentDate());
        rootObject.add("TopHat", clientInfo);

        // Add module settings
        for (Module.Category category : Module.Category.values()) {
            JsonObject categoryModules = new JsonObject();

            for (Module module : Client.moduleManager.getModulesByCategory(category)) {
                JsonObject moduleObject = new JsonObject();
                moduleObject.addProperty("Enabled", module.isEnabled());

                JsonObject settingModules = new JsonObject();

                for (Setting setting : Client.settingManager.getSettingsByModule(module)) {
                    if (setting instanceof DividerSetting) {
                        continue;
                    } else if (setting instanceof StringSetting) {
                        settingModules.addProperty(setting.getName(), ((StringSetting) setting).get());
                    } else if (setting instanceof NumberSetting) {
                        settingModules.addProperty(setting.getName(), ((NumberSetting) setting).get());
                    } else if (setting instanceof BooleanSetting) {
                        settingModules.addProperty(setting.getName(), ((BooleanSetting) setting).get());
                    }
                }

                // Add an empty "Settings" object if the module has no settings
                moduleObject.add("Settings", settingModules);

                categoryModules.add(module.getName(), moduleObject);
            }

            // Only add the category if it has modules
            if (!categoryModules.entrySet().isEmpty()) {
                rootObject.add(category.getName(), categoryModules);
            }
        }

        // Create a Gson instance with pretty printing
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(location + "/" + name + ".json")) {
            gson.toJson(rootObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to save the config.";
        }

        return String.format("Config saved as §e%s§r.", name);
    }


}
