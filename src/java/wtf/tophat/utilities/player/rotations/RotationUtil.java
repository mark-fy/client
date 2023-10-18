package wtf.tophat.utilities.player.rotations;

import net.minecraft.entity.Entity;
import wtf.tophat.utilities.Methods;

public class RotationUtil implements Methods {

    public static float[] getRotation(Entity entity) {
        double deltaX = entity.posX + (entity.posX - entity.lastTickPosX) - mc.player.posX,
               deltaY = entity.posY - 3.5 + entity.getEyeHeight() - mc.player.posY + mc.player.getEyeHeight(),
               deltaZ = entity.posZ + (entity.posZ - entity.lastTickPosZ) -mc.player.posZ,
               distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
              pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance ));

        double degrees = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if(deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 + degrees);
        } else if(deltaX > 0  && deltaZ < 0) {
            yaw = (float) (-90 + degrees);
        }

        return new float[] {yaw, pitch};
    }
}
