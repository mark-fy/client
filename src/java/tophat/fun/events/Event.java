package tophat.fun.events;

import tophat.fun.Client;

public class Event {

    private boolean cancelled;
    private State state;


    public Event() {
        this.cancelled = false;
        this.state = State.PRE;
    }

    public void call() {
        setCancelled(false);
        Client.INSTANCE.eventManager.post(this);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        PRE, POST
    }

}
