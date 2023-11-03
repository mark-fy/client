package wtf.tophat.commands.impl;

import wtf.tophat.TopHat;
import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;

@CommandInfo(name = "Script", description = "manage scripts", command = ".script <enable/disable/list> <script name>")
public class Script extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 2) {
            String subCommand = args[1];
            String scriptName = args.length >= 3 ? args[2] : "";

            switch (subCommand.toLowerCase()) {
                case "enable":
                    TopHat.scriptManager.enable(scriptName, "tophat/scripts");
                    break;
                case "disable":
                    TopHat.scriptManager.disable();
                    break;
                case "list":
                    sendChat(TopHat.scriptManager.list("tophat/scripts"), true);
                    break;
                default:
                    sendChat("Usage: .script <enable/disable/list> <script name>", true);
                    break;
            }
        } else {
            sendChat("Usage: .script <enable/disable/list> <script name>", true);
        }

        super.onCommand(args, command);
    }
}
