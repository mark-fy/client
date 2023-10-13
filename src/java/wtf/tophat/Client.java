package wtf.tophat;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import wtf.tophat.commands.base.CommandManager;
import wtf.tophat.events.base.EventManager;
import wtf.tophat.module.base.ModuleManager;
import wtf.tophat.settings.base.SettingManager;

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
        print("Starting Client...");
        Display.setTitle(getName() + " v" + getVersion());

        moduleManager.init();
        commandManager.init();
        settingManager.init();
        Minecraft.getMinecraft().settings.guiScale = 2;
        Minecraft.getMinecraft().settings.limitFramerate = 144;
        Minecraft.getMinecraft().settings.fullScreen = false;
    }

    public static void shutdown() {
        print("Shutting down TopHat!");
        Minecraft.getMinecraft().shutdownMinecraftApplet();
    }

    public static void print(String message) {
        if(message != null) {
            System.out.println("[TopHat-v" + getVersion() + "] " + message);
        }
    }
}
