package wtf.config.impl;

import wtf.config.base.Config;
import wtf.config.base.ConfigInfo;

import java.io.File;

@ConfigInfo(name = "delete", description = "delete configs")
public class Delete extends Config {

    public static String delete(String name, String location) {
        File configDirectory = new File(location);
        File[] files = configDirectory.listFiles();

        if (files == null || files.length == 0) {
            return "No files to delete.";
        }

        for (File file : files) {
            if (file.isFile() && file.getName().equals(name + ".json")) {
                if (file.delete()) {
                    String.format("Deleted §e%s§r successfully.", name);
                } else {
                    return "Failed to delete the config.";
                }
            }
        }

        return String.format("Config §e%s§r not found.", name);
    }
}
