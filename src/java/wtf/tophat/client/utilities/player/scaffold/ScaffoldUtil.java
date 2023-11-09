package wtf.tophat.client.utilities.player.scaffold;

import wtf.tophat.client.TopHat;
import wtf.tophat.client.modules.impl.move.Speed;
import wtf.tophat.client.utilities.Methods;

public class ScaffoldUtil implements Methods {

    public static double getYLevel() {
        if (!TopHat.moduleManager.getByClass(Speed.class).isEnabled()) {
            return mc.player.posY - 1.0;
        }
        return mc.player.posY - 1.0 >= 0.0D && Math.max(mc.player.posY, 0.0D) - Math.min(mc.player.posY, 0.0D) <= 3.0 && !mc.settings.keyBindJump.isKeyDown() ? 0.0D : mc.player.posY - 1.0;
    }

}