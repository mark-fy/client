package wtf.tophat.modules.impl.misc;
import wtf.tophat.TopHat;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.base.Module;
import wtf.tophat.settings.impl.StringSetting;
import io.github.nevalackin.radbus.Listen;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.player.chat.ChatUtil;
import wtf.tophat.events.base.Event;
import wtf.tophat.utilities.entity.EntityUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.modules.impl.combat.Killaura;

import java.util.Random;

@ModuleInfo(name= "Kill Message", desc = "Sends a message in chat when you kill an enemy player", category = Module.Category.MISC)
public class KillMessage extends Module {
    private final StringSetting mode;
    public KillMessage() {
        TopHat.settingManager.add(mode = new StringSetting(this, "mode", "GG", "Toxic"));


    }
    public String[] Killmessage = new String[]{
            "TopHat Owning?", "My gaming chair is way too good (and im wearing a TopHat", "TopHat just owned you!", "TopHat team is doing your mom!", "You suck TOPHAT ONTOP!", "Piped by TopHat"
    };
    @Listen
    public void onPacket(PacketEvent event){
        Random rnd = new Random();
        switch (mode.get()) {
            case "GG":
                if (Killaura.target.isDead = true); {

                    Methods.sendChat("GG");
                }
                break;
            case "toxic":
                    if (Killaura.target.isDead = true); {
                        mc.player.sendChatMessage(this.Killmessage[rnd.nextInt(this.Killmessage.length)]); {
            }
                break;
            }
            }
    }
}
