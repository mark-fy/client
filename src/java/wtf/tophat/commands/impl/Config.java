package wtf.tophat.commands.impl;

import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.config.impl.*;

@CommandInfo(name = "Config", description = "manage configs", command = ".config <save/load/list/delete> <config name>")
public class Config extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 2) {
            String subCommand = args[1];
            String configName = args.length >= 3 ? args[2] : "";

            switch (subCommand.toLowerCase()) {
                case "save":
                    sendChat(Save.save(configName, "tophat/configs"), true);
                    break;
                case "load":
                    sendChat(Load.load(configName, "tophat/configs"), true);
                    break;
                case "delete":
                    sendChat(Delete.delete(configName, "tophat/configs"), true);
                    break;
                case "list":
                    sendChat(List.list("tophat/configs"), true);
                    break;
                default:
                    sendChat("Usage: .config <save/load/list/delete> <config name>", true);
                    break;
            }
        } else {
            sendChat("Usage: .config <save/load/list/delete> <config name>", true);
        }

        super.onCommand(args, command);
    }
}
