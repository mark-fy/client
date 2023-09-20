package wtf.tophat.utilities.chat;

import net.minecraft.util.ChatComponentText;
import wtf.tophat.Client;
import wtf.tophat.utilities.Methods;

public class ChatUtil implements Methods {

    public static void addChatMessage(String message, boolean prefix) {
        message = prefix ? "§9" + Client.getName() + " §7>> §r" + message : message;
        mc.player.addChatMessage(new ChatComponentText(message));
    }

    public static void addChatMessage(String message) {
        addChatMessage(message, true);
    }

}
