package tophat.fun.commands.impl;

import tophat.fun.commands.Command;
import tophat.fun.commands.CommandAlias;
import tophat.fun.commands.CommandInfo;

@CommandAlias(alias = "hc")
@CommandInfo(name = "hclip", desc = "clip on the x axis.", command = ".hclip <blocks>")
public class HClip extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 2) {
            double blocks = Double.parseDouble(args[1]);

            if (blocks != 0) {
                double xChange = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * blocks;

                mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX + xChange, mc.thePlayer.posY, mc.thePlayer.posZ);

                String direction = (blocks > 0) ? "left" : "right";
                sendChat(String.format("Clipped you %s %s blocks", direction, blocks), true);
            } else {
                sendChat(String.format("Invalid number inputted: %s", blocks), true);
            }
        } else {
            sendChat(String.format("Usage: %s", getCommand()), true);
        }

        super.onCommand(args, command);
    }

}
