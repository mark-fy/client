package wtf.tophat.commands.impl;

import wtf.tophat.commands.base.Command;
import wtf.tophat.commands.base.CommandInfo;
import wtf.tophat.utilities.Methods;

@CommandInfo(name = "VClip", description = "teleport you down / up", command = ".vclip <height>")
public class VClip extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        double blocks = 0.0D;
        try {
            blocks = Double.parseDouble(command.split(" ")[1]);
        } catch (Exception e) {
            Methods.sendChat("Please enter a valid number!");
            return;
        }
        mc.player.setPosition(mc.player.posX, mc.player.posY + blocks, mc.player.posZ);
        Methods.sendChat("Teleported you " + blocks + " blocks " + ((blocks < 0.0D) ? "down." : "up."));
    }
}
