package wtf.tophat.utilities.player.chat;

import net.minecraft.util.ChatComponentText;
import wtf.tophat.TopHat;
import wtf.tophat.utilities.Methods;

public class ChatUtil implements Methods {

    public static void addChatMessage(String message, boolean prefix) {
        message = prefix ? "§9" + TopHat.getName() + " §7>> §r" + message : message;
        mc.player.addChatMessage(new ChatComponentText(message));
    }

    public static void addChatMessage(String message) {
        addChatMessage(message, true);
    }

}
