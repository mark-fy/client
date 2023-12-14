package dev.tophat.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface KeyListener {

    void onKey(final int key, final int scan, final int action, final int modifier);

    class KeyEvent extends AbstractEvent<KeyListener> {
        public static final int ID = 7;

        private final int key;
        private final int scan;
        private final int action;
        private final int modifier;

        public KeyEvent(final int key, final int scan, final int action, final int modifier) {
            this.key = key;
            this.scan = scan;
            this.action = action;
            this.modifier = modifier;
        }

        public int getKey() {
            return this.key;
        }

        public int getScan() {
            return this.scan;
        }

        public int getAction() {
            return this.action;
        }

        public int getModifier() {
            return this.modifier;
        }

        @Override
        public void call(final KeyListener listener) {
            listener.onKey(key, scan, action, modifier);
        }
    }
}
