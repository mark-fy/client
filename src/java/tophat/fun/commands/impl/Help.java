package tophat.fun.commands.impl;

import tophat.fun.Client;
import tophat.fun.commands.Command;
import tophat.fun.commands.CommandInfo;

import java.util.Arrays;

@CommandInfo(name = "help", desc = "outputs helpful information.", command = ".help")
public class Help extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        sendChat(String.format("Running %s %s made by %s\n", Client.CNAME, Client.CVERSION, Arrays.toString(Client.CAUTHORS)));

        for (Command cmd : Client.INSTANCE.commandManager.getList()) {
            sendChat(cmd.getCommand() + " - " + cmd.getDesc(), true);
        }

        super.onCommand(args,command);
    }

}
