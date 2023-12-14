package dev.tophat.event.network;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

public interface ReceivePacketListener {

    void onReceivePacket(final Packet<?> packet, final PacketListener listener);

    class ReceivePacketEvent extends CancellableEvent<ReceivePacketListener> {
        public static final int ID = 5;

        private Packet<?> packet;
        private PacketListener listener;

        public ReceivePacketEvent(final Packet<?> packet, final PacketListener listener) {
            this.packet = packet;
            this.listener = listener;
        }

        public Packet<?> getPacket() {
            return this.packet;
        }

        public void setPacket(final Packet<?> packet) {
            this.packet = packet;
        }

        public PacketListener getListener() {
            return this.listener;
        }

        public void setListener(final PacketListener listener) {
            this.listener = listener;
        }

        @Override
        public void call(final ReceivePacketListener receivePacketListener) {
            receivePacketListener.onReceivePacket(packet, listener);
        }
    }
}
