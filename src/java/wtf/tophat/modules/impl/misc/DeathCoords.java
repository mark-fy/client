package wtf.tophat.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.GuiGameOver;
import wtf.tophat.events.impl.OnDeathEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;

@ModuleInfo(name = "Death Coords", desc = "send a your death coords in the chat", category = Module.Category.MISC)
public class DeathCoords extends Module {

    @Listen
    public void onDeath(OnDeathEvent event) {
        if(event.getPlayer() == mc.player && mc.currentScreen instanceof GuiGameOver) {
            if (mc.player.deathTime < 1) {
                sendChat("Your death position - §cX: §r" + getX() + "§c Y: §r" + getY() + "§c Z: §r" + getZ(), true);
            }
        }
    }
}
