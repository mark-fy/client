package tophat.fun.events;

import io.github.nevalackin.radbus.PubSub;

public class EventManager {

    private final PubSub<Event> eventPubSub = PubSub.newInstance(System.err::println);

    public void subscribe(Object o) {
        eventPubSub.subscribe(o);
    }

    public void unsubscribe(Object o) {
        eventPubSub.unsubscribe(o);
    }

    public void post(Event o) {
        eventPubSub.publish(o);
    }

}
