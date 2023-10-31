package wtf.config.impl;

import wtf.config.base.Config;
import wtf.config.base.ConfigInfo;

import java.io.File;

@ConfigInfo(name = "list", description = "get a list of configs")
public class List extends Config {

    public static String list(String location) {
        File configDirectory = new File(location);
        File[] files = configDirectory.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null || files.length == 0) {
            return "No config files found.";
        }

        StringBuilder configNames = new StringBuilder("§e");
        for (File file : files) {
            configNames.append(file.getName(), 0, file.getName().length() - 5).append("§r, §e");
        }

        if (configNames.length() > 4) {
            configNames.delete(configNames.length() - 4, configNames.length());
        }

        return String.format("Available configs: §e%s§r.", configNames);
    }
}
