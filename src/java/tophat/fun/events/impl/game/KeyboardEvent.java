package tophat.fun.events.impl.game;

import tophat.fun.events.Event;

public class KeyboardEvent extends Event {

    public int keyCode;

    public KeyboardEvent(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

}
