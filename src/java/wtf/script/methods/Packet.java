package wtf.script.methods;

import wtf.tophat.utilities.Methods;

public class Packet implements Methods {

    // Methods

    public void send(net.minecraft.network.Packet<?> packet) { Methods.sendPacket(packet); }
    public void sendU(net.minecraft.network.Packet<?> packet) { sendPacketUnlogged(packet); }

    // Getters

    public int getPing() { return getPing(); }

}
