package tophat.fun;

import org.lwjgl.opengl.Display;
import tophat.fun.events.EventManager;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleManager;
import tophat.fun.modules.settings.SettingManager;
import tophat.fun.utilities.Methods;

import java.time.LocalDate;
import java.util.Arrays;

public enum Client implements Information, Methods {
    INSTANCE;

    public EventManager eventManager = new EventManager();
    public ModuleManager moduleManager = new ModuleManager();
    public SettingManager settingManager = new SettingManager();

    public void run() {
        LocalDate today = LocalDate.now();
        if (today.getDayOfMonth() == 18 && today.getMonthValue() == 11) {
            Display.setTitle(CNAME + " " + CVERSION + " | Happy proclamation day Latvia!");
        } else {
            Display.setTitle(CNAME + " " + CVERSION + " | Made by " + Arrays.toString(CAUTHORS));
        }
        moduleManager.init();
        settingManager.init();

        mc.gameSettings.limitFramerate = 122;
    }

}
