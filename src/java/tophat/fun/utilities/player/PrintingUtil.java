package tophat.fun.utilities.player;

import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import tophat.fun.Client;
import tophat.fun.utilities.Methods;

public class PrintingUtil implements Methods {

    public static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    public void addChatMessage(String message, boolean prefix) {
        message = prefix ? Client.CPREFIX + "§r" + message : message;
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }

    public void addChatMessage(String message) {
        addChatMessage(message, true);
    }

    public void addToConsole(String message) {
        if(message != null) {
            logger.info("[TopHat-" + Client.CVERSION + "]" + message);
        }
    }

    public void addToChat(String message, int state) {
        if(message != null && state != -1) {
            switch (state) {
                case 0: // DEFAULT
                    sendChat(message, true);
                    break;
                case 1: // ERROR
                    sendChat("§cError: §r" + message, true);
                    break;
                case 2: // WARNING
                    sendChat("§eWarning: §r" + message, true);
                    break;
            }
        }
    }

}
