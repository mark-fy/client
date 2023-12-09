package tophat.fun.commands.impl;

import tophat.fun.Client;
import tophat.fun.commands.Command;
import tophat.fun.commands.CommandAlias;
import tophat.fun.commands.CommandInfo;

import java.util.Arrays;

@CommandAlias(alias = "h")
@CommandInfo(name = "help", desc = "outputs helpful information.", command = ".help")
public class Help extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        sendChat(String.format("Running %s %s made by %s\n", Client.CNAME, Client.CVERSION, Arrays.toString(Client.CAUTHORS)), true);

        for (Command cmd : Client.INSTANCE.commandManager.getList()) {
            sendChat(String.format("%s - %s [%s]", cmd.getCommand(), cmd.getDesc(), cmd.getAlias()), true);
        }

        super.onCommand(args,command);
    }

}
