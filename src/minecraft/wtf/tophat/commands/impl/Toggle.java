package wtf.tophat.commands.impl;

import wtf.tophat.Client;
import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.module.base.Module;
import wtf.tophat.utilities.chat.ChatUtil;

@CommandInfo(name = "Toggle", description = "toggle modules", command = ".toggle <module>")
public class Toggle extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 2) {
            String moduleName = args[1];

            Module module = Client.moduleManager.getModule(moduleName);

            if (module != null) {
                module.toggle();
               sendChat(String.format("Toggled %s", moduleName), true);
            } else {
               sendChat(String.format("Module %s not found", moduleName), true);
            }
        } else {
           sendChat("Usage: .toggle <module>", true);
        }
        super.onCommand(args, command);
    }
}
