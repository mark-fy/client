package wtf.tophat.commands.impl;

import wtf.tophat.Client;
import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.utilities.chat.ChatUtil;

@CommandInfo(name = "Help", description = "gives helpful information", command = ".help")
public class Help extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        ChatUtil.addChatMessage("", false);
        ChatUtil.addChatMessage(String.format("Running %s v%s made by %s", Client.getName(), Client.getVersion(), "MarkGG"));
        ChatUtil.addChatMessage("", false);
        for(Command command1 : Client.commandManager.getList()) {
            ChatUtil.addChatMessage(command1.getCommand() + " - " + command1.getDescription());
        }
        ChatUtil.addChatMessage("", false);
        super.onCommand(args, command);
    }
}
