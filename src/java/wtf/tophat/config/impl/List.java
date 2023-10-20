package wtf.tophat.config.impl;

import wtf.tophat.config.base.Config;
import wtf.tophat.config.base.ConfigInfo;

import java.io.File;

@ConfigInfo(name = "list", description = "get a string list of available configs")
public class List extends Config {

    public static String getConfigs(String location) {
        File configDirectory = new File(location);
        File[] files = configDirectory.listFiles();

        if (files == null || files.length == 0) {
            return "No config files found.";
        }

        StringBuilder configNames = new StringBuilder("§e");
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                configNames.append(file.getName(), 0, file.getName().length() - 5).append("§r, §e");
            }
        }

        if (configNames.length() > 4) {
            configNames.delete(configNames.length() - 4, configNames.length());
        }

        return String.format("Available configs: §e%s§r.", configNames);
    }

}
