package wtf.tophat.client.commands.impl;

import wtf.tophat.client.commands.base.Command;
import wtf.tophat.client.commands.base.CommandInfo;

@CommandInfo(name = "VClip", description = "teleport you down / up", command = ".vclip <height>")
public class VClip extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        double blocks = 0.0D;
        try {
            blocks = Double.parseDouble(command.split(" ")[1]);
        } catch (Exception e) {
            sendChat("Please enter a valid number!");
            return;
        }
        mc.player.setPosition(mc.player.posX, mc.player.posY + blocks, mc.player.posZ);
        sendChat("Teleported you " + blocks + " blocks " + ((blocks < 0.0D) ? "down." : "up."));
    }
}
