package wtf.tophat.utilities.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.player.movement.MoveUtil;

import java.util.ArrayList;

public class DamageUtil implements Methods {

    public static void damage(DamageType type) {
        if (mc.player == null) return;
        double x = mc.player.posX, y = mc.player.posY, z = mc.player.posZ;
        switch (type) {
            case NCP:
                for (int i = 0; i <= MoveUtil.getMaxFallDist() / 0.0625; i++) {
                    mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0625, z, false));
                    mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                }
                mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
                break;
            case VERUS:
                mc.player.sendQueue.send(new C0BPacketEntityAction(mc.player, C0BPacketEntityAction.Action.STOP_SPRINTING));

                double val1 = 0;

                for (int i = 0; i <= 6; i++) {
                    val1 += 0.5;
                    mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY + val1, mc.player.posZ, true));
                }

                double val2 = mc.player.posY + val1;

                ArrayList<Float> vals = new ArrayList<>();

                vals.add(0.07840000152587834f);
                vals.add(0.07840000152587834f);
                vals.add(0.23052736891295922f);
                vals.add(0.30431682745754074f);
                vals.add(0.37663049823865435f);
                vals.add(0.44749789698342113f);
                vals.add(0.5169479491049742f);
                vals.add(0.5850090015087517f);
                vals.add(0.6517088341626192f);
                vals.add(0.1537296175885956f);

                for (float value : vals) {
                    val2 -= value;
                }

                mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, val2, mc.player.posZ, false));
                mc.player.sendQueue.send(new C03PacketPlayer(true));
                mc.player.sendQueue.send(new C0BPacketEntityAction(mc.player, C0BPacketEntityAction.Action.START_SPRINTING));

                mc.player.motionY = getMotion(0.42f);
        }
    }

    public static double getMotion(float baseMotionY) {
        Potion potion = Potion.jump;
        if (mc.player.isPotionActive(potion)) {
            int amplifier = mc.player.getActivePotionEffect(potion).getAmplifier();
            baseMotionY += (amplifier + 1) * 0.1F;
        }

        return baseMotionY;
    }

    public enum DamageType {
        NCP, VERUS
    }
}