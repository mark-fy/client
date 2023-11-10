package wtf.tophat.client.utilities.player;

import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.utilities.Methods;

public final class FallDistanceUtil implements Methods {

    public static float distance;
    private float lastDistance;

    public void onMotion(MotionEvent event) {
        if(event.getState().equals(Event.State.PRE)) {
            final float fallDistance = mc.player.fallDistance;

            if (fallDistance == 0) {
                distance = 0;
            }

            distance += fallDistance - lastDistance;
            lastDistance = fallDistance;
        }
    }
}