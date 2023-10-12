package wtf.tophat.events.base;

import io.github.nevalackin.radbus.PubSub;
import wtf.tophat.events.base.Event;

public class EventManager {

    private final PubSub<Event> eventPubSub = PubSub.newInstance(System.err::println);

    public void subscribe(Object o) {
        eventPubSub.subscribe(o);
    }

    public void unsubscribe(Object o) {
        eventPubSub.unsubscribe(o);
    }

    public void publish(Event o) {
        eventPubSub.publish(o);
    }

}
