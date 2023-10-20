package wtf.tophat;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import wtf.tophat.commands.base.CommandManager;
import wtf.tophat.config.impl.Load;
import wtf.tophat.config.impl.Save;
import wtf.tophat.events.base.EventManager;
import wtf.tophat.modules.base.ModuleManager;
import wtf.tophat.settings.base.SettingManager;
import wtf.tophat.utilities.player.chat.ChatUtil;

public enum Client {

    INSTANCE();

    private static final String name;
    private static final String version;

    static {
        name = "TopHat";
        version = "0.0.4";
    }

    Client() {}

    public static String getName() { return name; }

    public static String getVersion() { return version; }

    public static final ModuleManager moduleManager = new ModuleManager();
    public static final CommandManager commandManager = new CommandManager();
    public static final EventManager eventManager = new EventManager();
    public static final SettingManager settingManager = new SettingManager();

    public static void startup() {
        printL("Starting Client...");
        Display.setTitle(getName() + " v" + getVersion());

        moduleManager.init();
        commandManager.init();
        settingManager.init();
        Minecraft.getMinecraft().settings.guiScale = 2;
        Minecraft.getMinecraft().settings.limitFramerate = 144;
        Minecraft.getMinecraft().settings.fullScreen = false;

        printL(Load.load("default", "tophat"));
    }

    public static void shutdown() {
        printL("Shutting down TopHat!");
        printL(Save.save("default", "tophat"));
        Minecraft.getMinecraft().shutdownMinecraftApplet();
    }

    public static void printL(String message) {
        if(message != null) {
            System.out.println("[TopHat-v" + getVersion() + "] " + message);
        }
    }

    public static void printC(String message, int state) {
        if(message != null && state != -1) {
            switch (state) {
                case 0: // DEFAULT
                    ChatUtil.addChatMessage(message, true);
                    break;
                case 1: // ERROR
                    ChatUtil.addChatMessage("§cError: §r" + message, true);
                    break;
                case 2: // WARNING
                    ChatUtil.addChatMessage("§eWarning: §r" + message, true);
                    break;
            }
        }
    }
}
