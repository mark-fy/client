package tophat.fun;

import org.lwjgl.opengl.Display;
import tophat.fun.events.EventManager;

import java.util.Arrays;

public enum Client implements Information {
    INSTANCE;

    public EventManager eventManager;

    public void run() {
        Display.setTitle(CNAME + " v" + CVERSION + " | Presented by " + Arrays.toString(CAUTHORS));
        eventManager = new EventManager();
    }

}
