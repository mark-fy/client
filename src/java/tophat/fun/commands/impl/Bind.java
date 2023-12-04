package tophat.fun.commands.impl;

import org.lwjgl.input.Keyboard;
import tophat.fun.Client;
import tophat.fun.commands.Command;
import tophat.fun.commands.CommandInfo;
import tophat.fun.modules.Module;

import java.util.Locale;

@CommandInfo(name = "bind", desc = "bind a module to a key.", command = ".bind <module> <key>")
public class Bind extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 3) {
            String module = args[1];
            String key = args[2];

            Module module1 = Client.INSTANCE.moduleManager.getModule(module);

            if(module1 != null) {
                int key1 = Keyboard.getKeyIndex(key.toUpperCase(Locale.ROOT));
                module1.setKeyCode(key1);
                sendChat(String.format("Bound %s to %s", module1.getName(), Keyboard.getKeyName(key1)), true);
            } else {
                sendChat(String.format("Failed to find module %s", module), true);
            }
        } else {
            sendChat(String.format("Usage: %s", getCommand()), true);
        }

        super.onCommand(args,command);
    }

}
