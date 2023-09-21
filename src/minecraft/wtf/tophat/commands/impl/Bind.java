package wtf.tophat.commands.impl;

import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.module.base.Module;
import wtf.tophat.utilities.chat.ChatUtil;

import java.util.Locale;

@CommandInfo(name = "Bind", description = "binds modules to keys", command = ".bind <module> <key>")
public class Bind extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 3) {
            String moduleName = args[1];
            String keyName = args[2];

            Module module = Client.moduleManager.getModule(moduleName);

            if (module != null) {
                module.setKeyCode(Keyboard.getKeyIndex(keyName.toUpperCase(Locale.ROOT)));
                sendChat(String.format("Bound %s to %s", moduleName, Keyboard.getKeyName(module.getKeyCode())), true);
            } else {
                sendChat(String.format("Module %s not found", moduleName), true);
            }
        } else {
            sendChat("Usage: .bind <module> <key>", true);
        }

        super.onCommand(args, command);
    }

}
