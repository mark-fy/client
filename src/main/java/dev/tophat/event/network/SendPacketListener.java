package dev.tophat.event.network;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;

public interface SendPacketListener {

    void onSendPacket(final Packet<?> packet, final PacketCallbacks callbacks);

    class SendPacketEvent extends CancellableEvent<SendPacketListener> {
        public static final int ID = 6;

        private Packet<?> packet;
        private PacketCallbacks callbacks;

        public SendPacketEvent(final Packet<?> packet, final PacketCallbacks callbacks) {
            this.packet = packet;
            this.callbacks = callbacks;
        }

        public Packet<?> getPacket() {
            return this.packet;
        }

        public void setPacket(final Packet<?> packet) {
            this.packet = packet;
        }

        public PacketCallbacks getCallbacks() {
            return this.callbacks;
        }

        public void setCallbacks(final PacketCallbacks callbacks) {
            this.callbacks = callbacks;
        }

        @Override
        public void call(final SendPacketListener listener) {
            listener.onSendPacket(packet, callbacks);
        }
    }
}
