package wtf.tophat.config.impl;

import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParser;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.config.base.Config;
import wtf.tophat.config.base.ConfigInfo;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.settings.base.Setting;
import wtf.tophat.client.settings.impl.DividerSetting;

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
            JsonObject rootObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject clientInfo = rootObject.getAsJsonObject("TopHat");
            String version = clientInfo.get("Version").getAsString();

            if (!version.equalsIgnoreCase(TopHat.getVersion())) {
                TopHat.printC("Config was made in a different version of the client!", 2);
            }

            for (Module.Category category : Module.Category.values()) {
                JsonObject categoryModules = rootObject.getAsJsonObject(category.getName());

                for (Module module : TopHat.moduleManager.getModulesByCategory(category)) {
                    TopHat.printL("Module: " + module.getName());
                    JsonObject moduleObject = categoryModules.getAsJsonObject(module.getName());

                    if (moduleObject.has("Enabled")) {
                        boolean enabled = moduleObject.get("Enabled").getAsBoolean();
                        module.setEnabled(enabled);
                    } else {
                        // Set the default value to false if "Enabled" section is not present
                        module.setEnabled(false);
                    }

                    if (moduleObject.has("Settings")) {
                        JsonObject settingObject = moduleObject.getAsJsonObject("Settings");

                        for (Setting setting : TopHat.settingManager.getSettingsByModule(module)) {
                            try {
                                if (setting instanceof DividerSetting) {
                                    continue;
                                }

                                if (settingObject.has(setting.getName())) {
                                    if (setting instanceof StringSetting) {
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
                            } catch (Exception e) {
                                TopHat.printC("Failed to load setting for module " + module.getName(), 2);
                                e.printStackTrace();
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
            JsonObject rootObject = JsonParser.parseReader(reader).getAsJsonObject();

            for (Module.Category category : Module.Category.values()) {
                JsonObject categoryModules = rootObject.getAsJsonObject(category.getName());

                for (Module module : TopHat.moduleManager.getModulesByCategory(category)) {
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
