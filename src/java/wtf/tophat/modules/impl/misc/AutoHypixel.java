package wtf.tophat.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.StringUtils;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.utilities.time.TimeUtil;

@ModuleInfo(name = "Auto Hypixel", desc = "automatically navigate inside hypixel", category = Module.Category.EXPLOIT)
public class AutoHypixel extends Module {

    public String knownMode, knownType;
    private TimeUtil timer = new TimeUtil();

    @Listen
    public void onPacket(PacketEvent e) {
        if ((e.getPacket() instanceof S3EPacketTeams)) {
            String message = StringUtils.stripControlCodes(((S3EPacketTeams) e.getPacket()).getPrefix());
            if (message.equals("Mode: Normal")) {
                knownMode = "normal";
            }
            if (message.equals("Mode: Insane")) {
                knownMode = "insane";
            }
            if (message.equals("Mode: Mega")) {
                knownType = "mega";
                knownMode = "normal";
            }
            if (message.equals("Teams left")) {
                knownType = "teams";
            }
        }
        if (e.getPacket() instanceof S45PacketTitle) {
            if (((S45PacketTitle) e.getPacket()).getMessage() == null)
                return;

            String message = ((S45PacketTitle) e.getPacket()).getMessage().getUnformattedText();
            if (message.equals("YOU DIED!") || message.equals("GAME END") || message.equals("VICTORY!") || message.equals("You are now a spectator!")) {
                if (knownType != null && knownMode != null) {
                    if (timer.elapsed(2000, true)) {
                        mc.player.sendChatMessage("/play " + knownType + "_" + knownMode);
                    }
                }
            }
        }
        if (e.getPacket() instanceof S02PacketChat) {
            String message = ((S02PacketChat) e.getPacket()).getChatComponent().getUnformattedText();
            if (message.equals("Teaming is not allowed on Solo mode!")) {
                knownType = "solo";
            }
        }
    }
}