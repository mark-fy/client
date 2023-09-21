package wtf.tophat.commands.impl;

import wtf.tophat.Client;
import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.module.base.Module;
import wtf.tophat.settings.base.Setting;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.chat.ChatUtil;
import wtf.tophat.utilities.parser.BooleanParser;
import wtf.tophat.utilities.parser.NumberParser;

import java.util.Optional;

@CommandInfo(name = "Value", description = "set values", command = ".value <module> <value name> <value>")
public class Value extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 4) {
            String moduleName = args[1];
            String valueName = args[2];
            String newValue = args[3];

            Module module = Client.moduleManager.getModule(moduleName);

            if (module != null) {
                boolean found = false;
                for(Setting setting : Client.settingManager.getSettingsByModule(module)) {
                    if(setting.getName().replace(" ", "").equalsIgnoreCase(valueName) ) {
                        found = true;
                        if(setting instanceof BooleanSetting) {
                            BooleanSetting booleanSetting = (BooleanSetting) setting;
                            Optional<Boolean> optional = BooleanParser.parse(newValue);
                            if(optional != null) {
                                booleanSetting.setValue(optional.get());
                                sendChat(String.format("Value set to %s", newValue), true);
                            } else {
                                sendChat(String.format("Could not parse value %s", newValue), true);
                            }
                        } else if(setting instanceof NumberSetting) {
                            NumberSetting numberSetting = (NumberSetting) setting;
                            Number result = NumberParser.parse(newValue, numberSetting.getValue().getClass());
                            if(result != null) {
                                numberSetting.setValue(result);
                                sendChat(String.format("Value set to %s", newValue), true);
                            } else {
                                sendChat(String.format("Could not parse value %s", newValue), true);
                            }
                        } else if(setting instanceof StringSetting) {
                            StringSetting modeSetting = (StringSetting) setting;
                            modeSetting.setValue(args[2]);
                            sendChat(String.format("Value set to %s", newValue), true);
                        }
                        break;
                    }
                }
                if(!found) {
                    sendChat(String.format("Value %s not found", newValue), true);
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
