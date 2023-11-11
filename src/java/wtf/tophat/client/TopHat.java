package wtf.tophat.client;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.Display;
import wtf.tophat.client.commands.base.CommandManager;
import wtf.tophat.client.utilities.misc.bestnameuwu;
import wtf.tophat.config.ConfigManager;
import wtf.tophat.config.impl.Load;
import wtf.tophat.config.impl.Save;
import wtf.tophat.client.events.base.EventManager;
import wtf.tophat.client.modules.base.ModuleManager;
import wtf.tophat.script.ScriptManager;
import wtf.tophat.client.settings.base.SettingManager;
import wtf.tophat.client.utilities.player.chat.ChatUtil;
import wtf.tophat.viaversion.VersionManager;

import static wtf.tophat.client.utilities.Methods.createFolder;

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

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    public static final ModuleManager moduleManager = new ModuleManager();
    public static final SettingManager settingManager = new SettingManager();
    public static final CommandManager commandManager = new CommandManager();
    public static final EventManager eventManager = new EventManager();
    public static final ConfigManager configManager = new ConfigManager();
    public static final ScriptManager scriptManager = new ScriptManager();
    public static final VersionManager versionManager = new VersionManager();
    public static final bestnameuwu bestnameuwu = new bestnameuwu();

    private static final ChatUtil chatUtil = new ChatUtil();

    public static void startup() {
        printL("Starting Client...");
        Display.setTitle(getName() + " v" + getVersion());
        createFolder("tophat");

        bestnameuwu.init();
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
            logger.info("[TopHat-v" + getVersion() + "] " + message);
        }
    }

    public static void printC(String message, int state) {
        if(message != null && state != -1) {
            switch (state) {
                case 0: // DEFAULT
                    chatUtil.sendChat(message, true);
                    break;
                case 1: // ERROR
                    chatUtil.sendChat("§cError: §r" + message, true);
                    break;
                case 2: // WARNING
                    chatUtil.sendChat("§eWarning: §r" + message, true);
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
