package wtf.tophat.commands.impl;

import wtf.tophat.Client;
import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;

@CommandInfo(name = "Help", description = "gives helpful information", command = ".help")
public class Help extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        sendChat("");
        sendChat(String.format("Running %s v%s made by %s", Client.getName(), Client.getVersion(), "MarkGG"));
        sendChat("");
        for(Command command1 : Client.commandManager.getList()) {
            sendChat(command1.getCommand() + " - " + command1.getDescription(), true);
        }
        sendChat("");
        super.onCommand(args, command);
    }
}
