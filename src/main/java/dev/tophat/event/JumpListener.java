package dev.tophat.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface JumpListener {

    void onJump(final float yaw);

    class JumpEvent extends AbstractEvent<JumpListener> {
        public static final int ID = 3;
        private float yaw;

        public JumpEvent(final float yaw) {
            this.yaw = yaw;
        }

        public float getYaw() {
            return yaw;
        }

        @Override
        public void call(final JumpListener listener) {
            listener.onJump(yaw);
        }
    }
}
