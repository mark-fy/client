package tophat.fun.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import tophat.fun.utilities.math.MathUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public interface Methods {

    Minecraft mc = Minecraft.getMinecraft();

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    default boolean isMoving() {
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    default float getBPS() {
        float squareMotion = (float)(MathUtil.square(mc.thePlayer.motionX) + MathUtil.square(mc.thePlayer.motionZ));
        return (float)MathUtil.round(Math.sqrt(squareMotion) * 20.0D * mc.timer.timerSpeed, (int) 2.0D);
    }

    default int getPing() {
        NetworkPlayerInfo networkPlayerInfo = null;
        try {
            networkPlayerInfo = mc.getNetHandler().getPlayerInfoMap().stream().filter(player ->
                            player.getGameProfile().getId().toString().equals(this.mc.getNetHandler().getGameProfile().getId().toString()))
                    .findFirst().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (networkPlayerInfo != null) {
            return networkPlayerInfo.getResponseTime();
        } else {
            return -1;
        }
    }

    default String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

}
