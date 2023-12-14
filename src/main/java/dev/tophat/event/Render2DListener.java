package dev.tophat.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;

public interface Render2DListener {

    void onRender2D(final DrawContext context, final float tickDelta);

    class Render2DEvent extends AbstractEvent<Render2DListener> {
        public static final int ID = 4;

        private final DrawContext context;
        private final float tickDelta;

        public Render2DEvent(final DrawContext context, final float tickDelta) {
            this.context = context;
            this.tickDelta = tickDelta;
        }

        public DrawContext getDrawContext() {
            return this.context;
        }

        public float getTickDelta() {
            return this.tickDelta;
        }

        @Override
        public void call(final Render2DListener listener) {
            listener.onRender2D(context, tickDelta);
        }
    }
}
