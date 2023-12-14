package dev.tophat.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface TickListener {

    void onTick();

    class TickEvent extends AbstractEvent<TickListener> {
        public static final int ID = 0;

        @Override
        public void call(final TickListener listener) {
            listener.onTick();
        }
    }
}
