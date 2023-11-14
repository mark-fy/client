package tophat.fun.events.impl.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import tophat.fun.events.Event;

public class PacketEvent extends Event {

    private final Packet<?> packet;
    private final INetHandler iNetHandler;
    private final Type type;

    public PacketEvent(Packet<?> packet, INetHandler iNetHandler, Type type) {
        this.packet = packet;
        this.iNetHandler = iNetHandler;
        this.type = type;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public INetHandler getiNetHandler() {
        return iNetHandler;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        INCOMING, OUTGOING
    }

}
