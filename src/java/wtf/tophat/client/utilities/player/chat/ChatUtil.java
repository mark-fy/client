package wtf.tophat.client.utilities.player.chat;

import net.minecraft.util.ChatComponentText;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.utilities.Methods;

public class ChatUtil implements Methods {

    public void addChatMessage(String message, boolean prefix) {
        message = prefix ? "§9" + TopHat.getName() + " §7>> §r" + message : message;
        mc.player.addChatMessage(new ChatComponentText(message));
    }

    public void addChatMessage(String message) {
        addChatMessage(message, true);
    }

}
