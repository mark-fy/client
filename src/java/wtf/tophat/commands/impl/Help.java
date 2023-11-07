package wtf.tophat.commands.impl;

import wtf.tophat.TopHat;
import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.utilities.Methods;

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
