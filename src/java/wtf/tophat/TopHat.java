package wtf.tophat;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import wtf.tophat.commands.base.CommandManager;
import wtf.config.ConfigManager;
import wtf.config.impl.Load;
import wtf.config.impl.Save;
import wtf.tophat.events.base.EventManager;
import wtf.tophat.modules.base.ModuleManager;
import wtf.script.ScriptManager;
import wtf.tophat.settings.base.SettingManager;
import wtf.tophat.utilities.player.chat.ChatUtil;
import wtf.viaversion.VersionManager;

import static wtf.tophat.utilities.Methods.createFolder;

public enum TopHat {

    INSTANCE();

    private static final String name;
    private static final String version;

    static {
        name = "TopHat";
        version = "0.0.5";
    }

    TopHat() {}

    public static String getName() { return name; }

    public static String getVersion() { return version; }

    public static final ModuleManager moduleManager = new ModuleManager();
    public static final SettingManager settingManager = new SettingManager();
    public static final CommandManager commandManager = new CommandManager();
    public static final EventManager eventManager = new EventManager();
    public static final ConfigManager configManager = new ConfigManager();
    public static final ScriptManager scriptManager = new ScriptManager();
    public static final VersionManager versionManager = new VersionManager();

    public static void startup() {
        printL("Starting Client...");
        Display.setTitle(getName() + " v" + getVersion());
        createFolder("tophat");

        versionManager.init();
        moduleManager.init();
        commandManager.init();
        settingManager.init();
        configManager.init();
        scriptManager.init();

        Minecraft.getMinecraft().settings.guiScale = 2;
        Minecraft.getMinecraft().settings.limitFramerate = 144;
        Minecraft.getMinecraft().settings.fullScreen = false;

        load();
    }

    public static void shutdown() {
        save();
        printL("Shutting down TopHat!");
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

    public static void load() {
        printL(Load.load("default", "tophat"));
        printL(Load.load("tophat"));
    }

    public static void save() {
        printL(Save.save("default", "tophat"));
        printL(Save.save("tophat"));
    }


}
