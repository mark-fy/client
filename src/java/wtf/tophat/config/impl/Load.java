package wtf.tophat.config.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import wtf.tophat.Client;
import wtf.tophat.config.base.Config;
import wtf.tophat.config.base.ConfigInfo;
import wtf.tophat.modules.base.Module;
import wtf.tophat.settings.base.Setting;
import wtf.tophat.settings.impl.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@ConfigInfo(name = "load", description = "load module states")
public class Load extends Config {

    public static String load(String name, String location) {
        File configFile = new File(location + "/" + name + ".json");

        if (!configFile.exists() || !configFile.isFile()) {
            return "Config file not found: " + configFile.getAbsolutePath();
        }

        try (FileReader reader = new FileReader(configFile)) {
            JsonParser parser = new JsonParser();
            JsonObject rootObject = parser.parse(reader).getAsJsonObject();

            // Load Client information
            if (rootObject.has("TopHat")) {
                JsonObject clientInfo = rootObject.getAsJsonObject("TopHat");
                String version = clientInfo.get("Version").getAsString();
                //String date = clientInfo.get("Date").getAsString();

                if(!version.equalsIgnoreCase(Client.getVersion())) {
                    Client.printC("Config was made in a different version of the client!", 2);
                }
            }

            // Load module settings
            for (Module.Category category : Module.Category.values()) {
                if (rootObject.has(category.getName())) {
                    JsonObject categoryModules = rootObject.getAsJsonObject(category.getName());

                    for (Module module : Client.moduleManager.getModulesByCategory(category)) {
                        if (categoryModules.has(module.getName())) {
                            JsonObject moduleObject = categoryModules.getAsJsonObject(module.getName());

                            // Load module enabled state
                            if (moduleObject.has("Enabled")) {
                                boolean enabled = moduleObject.get("Enabled").getAsBoolean();
                                module.setEnabled(enabled);
                            }

                            // Load setting values
                            for (Setting setting : Client.settingManager.getSettingsByModule(module)) {
                                if (moduleObject.has("Settings") && moduleObject.getAsJsonObject("Settings").has(setting.getName())) {
                                    if (setting instanceof DividerSetting) {
                                        continue;
                                    } else if (setting instanceof StringSetting) {
                                        String value = moduleObject.getAsJsonObject("Settings").get(setting.getName()).getAsString();
                                        ((StringSetting) setting).set(value);
                                    } else if (setting instanceof BooleanSetting) {
                                        boolean value = moduleObject.getAsJsonObject("Settings").get(setting.getName()).getAsBoolean();
                                        ((BooleanSetting) setting).set(value);
                                    } else if(setting instanceof NumberSetting) {
                                        Number value = moduleObject.getAsJsonObject("Settings").get(setting.getName()).getAsNumber();
                                        ((NumberSetting) setting).set(value);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return "Config " + name + " was loaded.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to load the config: " + e.getMessage();
        }
    }

}
