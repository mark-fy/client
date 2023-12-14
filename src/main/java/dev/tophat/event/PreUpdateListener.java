package dev.tophat.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface PreUpdateListener {

    void onUpdate();

    class PreUpdateEvent extends AbstractEvent<PreUpdateListener> {
        public static final int ID = 0;

        @Override
        public void call(final PreUpdateListener listener) {
            listener.onUpdate();
        }
    }
}
