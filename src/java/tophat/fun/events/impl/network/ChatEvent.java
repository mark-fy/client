package tophat.fun.events.impl.network;

import tophat.fun.events.Event;

public class ChatEvent extends Event {

    private String message;

    public ChatEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
