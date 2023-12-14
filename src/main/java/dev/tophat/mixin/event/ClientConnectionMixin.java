package dev.tophat.mixin.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import dev.tophat.event.network.ReceivePacketListener;
import dev.tophat.event.network.SendPacketListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {

    @Shadow
    private static <T extends PacketListener> void handlePacket(final Packet<?> packet, final PacketListener listener) {}

    @Inject(
            method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/ClientConnection;isOpen()Z",
                    shift = At.Shift.AFTER
            ), cancellable = true
    )

    public void onNetworkWrite(final Packet<?> packet, final PacketCallbacks callbacks, final boolean flush, final CallbackInfo callbackInfo) {
        final SendPacketListener.SendPacketEvent sendPacketEvent = new SendPacketListener.SendPacketEvent(packet, callbacks);
        DietrichEvents2.global().post(SendPacketListener.SendPacketEvent.ID, sendPacketEvent);

        if (sendPacketEvent.isCancelled())
            callbackInfo.cancel();
    }

    @Redirect(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V")
    )
    public void onHandlePacket(final Packet<?> packet, final PacketListener listener) {
        final ReceivePacketListener.ReceivePacketEvent receivePacketEvent = new ReceivePacketListener.ReceivePacketEvent(packet, listener);
        DietrichEvents2.global().post(ReceivePacketListener.ReceivePacketEvent.ID, receivePacketEvent);

        if (receivePacketEvent.isCancelled())
            return;

        handlePacket(receivePacketEvent.getPacket(), receivePacketEvent.getListener());
    }

}
