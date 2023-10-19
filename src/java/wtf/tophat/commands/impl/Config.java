package wtf.tophat.commands.impl;

import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.config.impl.Save;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

@CommandInfo(name = "Config", description = "save or load configs", command = ".config <save/load> <config name>")
public class Config extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 3) {
            String configName = args[2].toLowerCase(Locale.ROOT);
            String directoryName = "tophat";

            Path directoryPath = Paths.get(directoryName);
            if (!Files.exists(directoryPath)) {
                try {
                    Files.createDirectory(directoryPath);
                } catch (IOException e) {
                    e.printStackTrace();
                    sendChat("Failed to create the directory.", true);
                    return;
                }
            }

            if (args[1].equalsIgnoreCase("save")) {
                sendChat(Save.save(configName, "tophat"), true);
            } else if (args[1].equalsIgnoreCase("load")) {
                // Handle config loading here
            } else {
                sendChat("Usage: .config <save/load> <config name>", true);
            }
        } else {
            sendChat("Usage: .config <save/load> <config name>", true);
        }
        super.onCommand(args, command);
    }
}
