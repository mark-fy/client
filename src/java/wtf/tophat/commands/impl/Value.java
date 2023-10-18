package wtf.tophat.commands.impl;

import wtf.tophat.Client;
import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.modules.base.Module;
import wtf.tophat.settings.base.Setting;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.math.parser.BooleanParser;
import wtf.tophat.utilities.math.parser.NumberParser;

import java.util.Optional;

@CommandInfo(name = "Value", description = "set values", command = ".value <module> <value name> <value>")
public class Value extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 4) {
            StringBuilder moduleNameBuilder = new StringBuilder();
            for (int i = 1; i < args.length - 2; i++) {
                moduleNameBuilder.append(args[i]).append(" ");
            }
            String moduleName = moduleNameBuilder.toString().trim();
            String valueName = args[args.length - 2];
            String newValue = args[args.length - 1];

            Module module = Client.moduleManager.getModule(moduleName);

            if (module != null) {
                boolean found = false;
                for (Setting setting : Client.settingManager.getSettingsByModule(module)) {
                    if (setting.getName().equalsIgnoreCase(valueName)) {
                        found = true;
                        if (setting instanceof BooleanSetting) {
                            BooleanSetting booleanSetting = (BooleanSetting) setting;
                            Optional<Boolean> optional = BooleanParser.parse(newValue);
                            if (optional != null) {
                                booleanSetting.set(optional.get());
                                sendChat(String.format("Value set to %s", newValue), true);
                            } else {
                                sendChat(String.format("Could not parse value %s", newValue), true);
                            }
                        } else if (setting instanceof NumberSetting) {
                            NumberSetting numberSetting = (NumberSetting) setting;
                            Number result = NumberParser.parse(newValue, numberSetting.get().getClass());
                            if (result != null) {
                                numberSetting.set(result);
                                sendChat(String.format("Value set to %s", newValue), true);
                            } else {
                                sendChat(String.format("Could not parse value %s", newValue), true);
                            }
                        } else if (setting instanceof StringSetting) {
                            StringSetting stringSetting = (StringSetting) setting;
                            stringSetting.set(newValue);
                            sendChat(String.format("Value set to %s", newValue), true);
                        }
                        break;
                    }
                }
                if (!found) {
                    sendChat(String.format("Value %s not found", valueName), true);
                }
            } else {
                sendChat(String.format("Module %s not found", moduleName), true);
            }
        } else {
            sendChat("Usage: .value <module> <value name> <value>", true);
        }
        super.onCommand(args, command);
    }
}
