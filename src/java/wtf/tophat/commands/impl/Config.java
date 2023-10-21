package wtf.tophat.commands.impl;

import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.config.impl.List;
import wtf.tophat.config.impl.Load;
import wtf.tophat.config.impl.Save;

@CommandInfo(name = "Config", description = "save or load configs", command = ".config <save/load/list> <config name>")
public class Config extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 3) {
            String configName = args[2];

            if (args[1].equalsIgnoreCase("save")) {
                sendChat(Save.save(configName, "tophat/configs"), true);
            } else if (args[1].equalsIgnoreCase("load")) {
                sendChat(Load.load(configName, "tophat/configs"), true);
            } else {
                sendChat("Usage: .config <save/load/list> <config name>", true);
            }
        } else if(args.length == 2) {
            if (args[1].equalsIgnoreCase("list")) {
                sendChat(List.getConfigs("tophat/configs"), true);
            } else {
                sendChat("Usage: .config <save/load/list> <config name>", true);
            }
        } else {
            sendChat("Usage: .config <save/load/list> <config name>", true);
        }
        super.onCommand(args, command);
    }
}
