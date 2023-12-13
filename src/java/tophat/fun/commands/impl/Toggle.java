package tophat.fun.commands.impl;

import tophat.fun.Client;
import tophat.fun.commands.base.Command;
import tophat.fun.commands.base.CommandAlias;
import tophat.fun.commands.base.CommandInfo;
import tophat.fun.modules.base.Module;

@CommandAlias(alias = "t")
@CommandInfo(name = "toggle", desc = "toggle a module.", command = ".toggle <module>")
public class Toggle extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 2) {
            String module = args[1];
            Module module1 = Client.INSTANCE.moduleManager.getModule(module);

            if(module1 != null) {
                module1.toggle();
                sendChat(String.format("Toggled %s %s", module1.getName(), module1.isEnabled() ? "on" : "off"), true);
            } else {
                sendChat(String.format("Module %s not found", module), true);
            }
        } else {
            sendChat(String.format("Usage: %s", getCommand()), true);
        }

        super.onCommand(args,command);
    }

}
