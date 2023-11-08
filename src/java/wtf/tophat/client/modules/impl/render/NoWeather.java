package wtf.tophat.client.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.world.storage.WorldInfo;
import wtf.tophat.client.events.impl.PacketEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;

@ModuleInfo(name = "No Weather", desc = "remove all weather from your game", category = Module.Category.RENDER)
public class NoWeather extends Module {

    @Listen
    public void onPacket(PacketEvent e){
        if(getPlayer() == null || getWorld() == null) return;
        WorldInfo worldinfo = getWorld().getWorldInfo();

        worldinfo.setCleanWeatherTime(0);
        worldinfo.setRainTime(0);
        worldinfo.setThunderTime(0);
        worldinfo.setRaining(false);
        worldinfo.setThundering(false);

        if(e.getPacket() instanceof S2BPacketChangeGameState){
            e.setCancelled(true);
        }
    }
}