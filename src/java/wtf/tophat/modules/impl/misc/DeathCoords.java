package wtf.tophat.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.util.EnumChatFormatting;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.utilities.Methods;

@ModuleInfo(name = "Death Coords", desc = "send a your death coords in the chat", category = Module.Category.MISC)
public class DeathCoords extends Module {

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mc.player.getHealth() < 1.0f && mc.currentScreen instanceof GuiGameOver) {
            int x = mc.player.getPosition().getX();
            int y = mc.player.getPosition().getY();
            int z = mc.player.getPosition().getZ();
            if (mc.player.deathTime < 1) {
                Methods.sendChat("Your death position - " + EnumChatFormatting.RED + "X: " + EnumChatFormatting.RESET + x + EnumChatFormatting.RED + " Y: " + EnumChatFormatting.RESET + y + EnumChatFormatting.RED + " Z: " + EnumChatFormatting.RESET + z);
            }
        }
    }
}
