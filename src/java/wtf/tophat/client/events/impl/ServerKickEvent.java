package wtf.tophat.client.events.impl;

import wtf.tophat.client.events.base.Event;

import java.util.List;

public class ServerKickEvent extends Event {

    private List<String> message;

    public ServerKickEvent(List<String> message) {
        this.message = message;
    }

    public List<String> getMessage() {
        return message;
    }
}
