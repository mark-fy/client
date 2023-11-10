package wtf.tophat.client.events.impl.game;

import wtf.tophat.client.events.base.Event;

public class KeyboardEvent extends Event {

    private int keyCode;

    public KeyboardEvent(int keyCode) { this.keyCode = keyCode; }

    public int getKeyCode() { return keyCode; }

    public void setKeyCode(int keyCode) { this.keyCode = keyCode; }

}
