package dev.tophat.event;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.client.gui.screen.Screen;

public interface OpenScreenListener {

    void onOpenScreen(final Screen screen);

    class OpenScreenEvent extends CancellableEvent<OpenScreenListener> {
        public static final int ID = 2;
        private final Screen screen;

        public OpenScreenEvent(final Screen screen) {
            this.screen = screen;
        }

        public Screen getScreen() {
            return this.screen;
        }

        @Override
        public void call(final OpenScreenListener listener) {
            listener.onOpenScreen(screen);
        }
    }
}
