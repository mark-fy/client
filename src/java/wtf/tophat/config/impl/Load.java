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

@ConfigInfo(name = "load", description = "load configs")
public class Load extends Config {

    public static String load(String name, String location) {
        File configFile = new File(location + "/" + name + ".json");

        if (!configFile.exists() || !configFile.isFile()) {
            return "Config file not found: " + configFile.getAbsolutePath();
        }

        try (FileReader reader = new FileReader(configFile)) {
            JsonObject rootObject = new JsonParser().parse(reader).getAsJsonObject();
            JsonObject clientInfo = rootObject.getAsJsonObject("TopHat");
            String version = clientInfo.get("Version").getAsString();

            if (!version.equalsIgnoreCase(Client.getVersion())) {
                Client.printC("Config was made in a different version of the client!", 2);
            }

            for (Module.Category category : Module.Category.values()) {
                JsonObject categoryModules = rootObject.getAsJsonObject(category.name());

                for (Module module : Client.moduleManager.getModulesByCategory(category)) {
                    JsonObject moduleObject = categoryModules.getAsJsonObject(module.getName());

                    if (moduleObject.has("Enabled")) {
                        boolean enabled = moduleObject.get("Enabled").getAsBoolean();
                        module.setEnabled(enabled);
                    }

                    for (Setting setting : Client.settingManager.getSettingsByModule(module)) {
                        if (moduleObject.has("Settings")) {
                            JsonObject settingObject = moduleObject.getAsJsonObject("Settings");
                            if (settingObject.has(setting.getName())) {
                                if (setting instanceof DividerSetting) {
                                    continue;
                                } else if (setting instanceof StringSetting) {
                                    String value = settingObject.get(setting.getName()).getAsString();
                                    ((StringSetting) setting).set(value);
                                } else if (setting instanceof BooleanSetting) {
                                    boolean value = settingObject.get(setting.getName()).getAsBoolean();
                                    ((BooleanSetting) setting).set(value);
                                } else if (setting instanceof NumberSetting) {
                                    Number value = settingObject.get(setting.getName()).getAsNumber();
                                    ((NumberSetting) setting).set(value);
                                }
                            }
                        }
                    }
                }
            }

            return String.format("Config §e%s§r was loaded.", name);
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to load the config: " + e.getMessage();
        }
    }

    public static String load(String location) {
        File configFile = new File(location + "/keybinds.json");

        if (!configFile.exists() || !configFile.isFile()) {
            return "Keybind file not found: " + configFile.getAbsolutePath();
        }

        try (FileReader reader = new FileReader(configFile)) {
            JsonObject rootObject = new JsonParser().parse(reader).getAsJsonObject();

            for (Module.Category category : Module.Category.values()) {
                JsonObject categoryModules = rootObject.getAsJsonObject(category.name());

                for (Module module : Client.moduleManager.getModulesByCategory(category)) {
                    JsonObject moduleObject = categoryModules.getAsJsonObject(module.getName());

                    if (moduleObject.has("Keybind")) {
                        int keycode = moduleObject.get("Keybind").getAsInt();
                        module.setKeyCode(keycode);
                    }
                }
            }

            return "Keybindings were successfully loaded.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to load the config: " + e.getMessage();
        }
    }
}
