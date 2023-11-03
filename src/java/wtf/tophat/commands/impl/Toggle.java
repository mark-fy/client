package wtf.tophat.commands.impl;

import wtf.tophat.TopHat;
import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.modules.base.Module;

import java.util.Arrays;

@CommandInfo(name = "Toggle", description = "toggle modules", command = ".toggle <module>")
public class Toggle extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 2) {
            String moduleName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            Module module = TopHat.moduleManager.getModule(moduleName);

            if (module != null) {
                module.toggle();
                sendChat(String.format("Toggled %s %s", module.getName(), module.isEnabled() ? "on" : "off"), true);
            } else {
                sendChat(String.format("Module %s not found", moduleName), true);
            }
        } else {
            sendChat("Usage: .toggle <module>", true);
        }
        super.onCommand(args, command);
    }
}
