package wtf.tophat.client.commands.impl;

import wtf.tophat.client.TopHat;
import wtf.tophat.client.commands.base.Command;
import wtf.tophat.client.commands.base.CommandInfo;

@CommandInfo(name = "Help", description = "gives helpful information", command = ".help")
public class Help extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        sendChat(String.format("Running %s v%s made by %s", TopHat.getName(), TopHat.getVersion(), "MarkGG"));

        for (Command command1 : TopHat.commandManager.getList()) {
            sendChat(command1.getCommand() + " - " + command1.getDescription(), true);
        }

        super.onCommand(args, command);
    }
}
