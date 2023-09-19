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
                ChatUtil.addChatMessage(String.format("Bound %s to %s", moduleName, Keyboard.getKeyName(module.getKeyCode())));
            } else {
                ChatUtil.addChatMessage(String.format("Module %s not found", moduleName));
            }
        } else {
            ChatUtil.addChatMessage("Usage: .bind <module> <key>");
        }

        super.onCommand(args, command);
    }

}
