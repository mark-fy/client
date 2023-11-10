package wtf.tophat.client.events.impl.game;

import wtf.tophat.client.events.base.Event;

public class ChatEvent extends Event {

    private String message;

    public ChatEvent(String message) { this.message = message; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

}
