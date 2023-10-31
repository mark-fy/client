package wtf.script.methods;

import wtf.tophat.utilities.Methods;

import java.awt.*;
import java.awt.event.InputEvent;

public class Mouse implements Methods {

    // Methods

    public void clickLeft() {
        try {
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void clickRight() {
        try {
            Robot robot = new Robot();
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}
