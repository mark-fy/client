package wtf.tophat.commands.impl;

import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.modules.base.Module;

import java.util.Arrays;
import java.util.Locale;

@CommandInfo(name = "Bind", description = "bind modules to keys", command = ".bind <module> <key>")
public class Bind extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 3) {
            String moduleName = String.join(" ", Arrays.copyOfRange(args, 1, args.length - 1));
            String keyName = args[args.length - 1];
            Module module = Client.moduleManager.getModule(moduleName);

            if (module != null) {
                int keyCode = Keyboard.getKeyIndex(keyName.toUpperCase(Locale.ROOT));
                module.setKeyCode(keyCode);
                sendChat(String.format("Bound %s to %s", module.getName(), Keyboard.getKeyName(module.getKeyCode())), true);
            } else {
                sendChat(String.format("Module %s not found", moduleName), true);
            }
        } else {
            sendChat("Usage: .bind <module> <key>", true);
        }

        super.onCommand(args, command);
    }
}
