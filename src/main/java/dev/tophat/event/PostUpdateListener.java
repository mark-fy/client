package dev.tophat.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface PostUpdateListener {

    void onPostUpdate();

    class PostUpdateEvent extends AbstractEvent<PostUpdateListener> {
        public static final int ID = 1;

        @Override
        public void call(final PostUpdateListener listener) {
            listener.onPostUpdate();
        }
    }
}
