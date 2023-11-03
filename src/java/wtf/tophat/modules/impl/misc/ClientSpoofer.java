package wtf.tophat.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import wtf.tophat.Client;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.StringSetting;

@ModuleInfo(name = "Client Spoofer",desc = "spoofs your client brand", category = Module.Category.MISC)
public class ClientSpoofer extends Module {

    private final StringSetting mode;

    public ClientSpoofer() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "Lunar", "Lunar", "Forge", "LabyMod", "CheatBreaker", "PvP Lounge", "Geyser")
        );
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if(event.getPacket() instanceof C17PacketCustomPayload) {
            C17PacketCustomPayload packet = (C17PacketCustomPayload) event.getPacket();

            switch(mode.get()) {
                case "Lunar":
                    packet.setChannel("REGISTER");
                    packet.setData(createPacketBuffer("Lunar-Client", false));
                    break;
                case "Forge":
                    packet.setData(createPacketBuffer("FML", true));
                    break;
                case "LabyMod":
                    packet.setData(createPacketBuffer("LMC", true));
                    break;
                case "CheatBreaker":
                    packet.setData(createPacketBuffer("CB", true));
                    break;
                case "PvP Lounge":
                    packet.setData(createPacketBuffer("PLC18", false));
                    break;
                case "Geyser":
                    packet.setData(createPacketBuffer("Geyser", false));
                    break;
            }
        }
    }

    private PacketBuffer createPacketBuffer(String data, boolean string) {
        if (string) {
            return new PacketBuffer(Unpooled.buffer()).writeString(data);
        }else {
            return new PacketBuffer(Unpooled.wrappedBuffer(data.getBytes()));
        }
    }
}
