package tophat.fun;

import org.lwjgl.opengl.Display;
import tophat.fun.events.EventManager;
import tophat.fun.modules.ModuleManager;
import tophat.fun.modules.settings.SettingManager;

import java.util.Arrays;

public enum Client implements Information {
    INSTANCE;

    public EventManager eventManager = new EventManager();
    public ModuleManager moduleManager = new ModuleManager();
    public SettingManager settingManager = new SettingManager();

    public void run() {
        Display.setTitle(CNAME + " v" + CVERSION + " | Made with love by " + Arrays.toString(CAUTHORS));
        moduleManager.init();
        settingManager.init();
    }

}
