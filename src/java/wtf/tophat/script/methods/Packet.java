package wtf.tophat.script.methods;

import wtf.tophat.client.utilities.Methods;

public class Packet implements Methods {

    // Methods

    public void send(net.minecraft.network.Packet<?> packet) { sendPacket(packet); }
    public void sendU(net.minecraft.network.Packet<?> packet) { sendPacketUnlogged(packet); }

    // Getters

    public int getPing() { return getPing(); }

}