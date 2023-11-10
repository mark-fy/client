package wtf.tophat.client.modules.impl.misc;

import java.util.Random;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import wtf.tophat.client.events.impl.network.PacketEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;

@ModuleInfo(name = "Kill Says", desc = "says toxic messages when you kill someone", category = Module.Category.MISC)
public class KillSays extends Module {
    private final String[] messages = new String[]{
            "C00KeepAlive bypassing :skull:", "bad ac", "get TopHat for being good", "tophat?xyz (soon)", "ezzzzzz"
    };

    @Listen
    public void onPacket(PacketEvent event) {
        Packet<?> e = event.getPacket();
        Random rnd = new Random();
        if (e instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat)e;
            String cp21 = s02PacketChat.getChatComponent().getUnformattedText();
            if (cp21.contains("was killed by " + mc.session.getUsername())) {
                mc.player.sendChatMessage(this.messages[rnd.nextInt(this.messages.length)]);
            }

            if (cp21.contains("was slain by " + mc.session.getUsername())) {
                mc.player.sendChatMessage(this.messages[rnd.nextInt(this.messages.length)]);
            }

            if (cp21.contains("was shot and killed by " + mc.session.getUsername())) {
                mc.player.sendChatMessage(this.messages[rnd.nextInt(this.messages.length)]);
            }

            if (cp21.contains("was snowballed to death by " + mc.session.getUsername())) {
                mc.player.sendChatMessage(this.messages[rnd.nextInt(this.messages.length)]);
            }

            if (cp21.contains("was killed with magic by " + mc.session.getUsername())) {
                mc.player.sendChatMessage(this.messages[rnd.nextInt(this.messages.length)]);
            }

            if (cp21.contains("was killed with an explosion by " + mc.session.getUsername())) {
                mc.player.sendChatMessage(this.messages[rnd.nextInt(this.messages.length)]);
            }

            if (cp21.contains("was killed with a potion by " + mc.session.getUsername())) {
                mc.player.sendChatMessage(this.messages[rnd.nextInt(this.messages.length)]);
            }
        }
    }
}