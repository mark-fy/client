package tophat.fun;

import org.lwjgl.opengl.Display;
import tophat.fun.events.EventManager;
import tophat.fun.modules.ModuleManager;
import tophat.fun.utilities.Methods;

import java.time.LocalDate;
import java.util.Arrays;

public enum Client implements Information, Methods {
    INSTANCE;

    public final EventManager eventManager = new EventManager();
    public final ModuleManager moduleManager = new ModuleManager();

    public void run() {
        Methods.createFolder("tophat");

        LocalDate today = LocalDate.now();
        if (today.getDayOfMonth() == 18 && today.getMonthValue() == 11) {
            Display.setTitle(CNAME + " " + CVERSION + " | Happy proclamation day Latvia!");
        } else {
            Display.setTitle(CNAME + " " + CVERSION + " | Made by " + Arrays.toString(CAUTHORS));
        }
        moduleManager.init();

        mc.gameSettings.limitFramerate = 144;
    }

    public void end() {
        chatUtil.addToConsole("Shutting down TopHat!");
    }

}
