package tophat.fun.commands.impl;

import tophat.fun.commands.base.Command;
import tophat.fun.commands.base.CommandAlias;
import tophat.fun.commands.base.CommandInfo;

@CommandAlias(alias = "vc")
@CommandInfo(name = "vclip", desc = "clip on the y axis.", command = ".vclip <blocks>")
public class VClip extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 2) {
            double blocks = Double.parseDouble(args[1]);

            if(blocks != 0) {
                mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + blocks, mc.thePlayer.posZ);
                sendChat(String.format("Clipped you %s %s blocks", blocks < 0 ? "down" : "up", blocks), true);
            } else {
                sendChat(String.format("Invalid number inputted: %s", blocks), true);
            }
        } else {
            sendChat(String.format("Usage: %s", getCommand()), true);
        }

        super.onCommand(args,command);
    }

}
