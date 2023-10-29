package wtf.tophat.utilities.player.scaffold;

import wtf.tophat.Client;
import wtf.tophat.modules.impl.move.Speed;
import wtf.tophat.utilities.Methods;

public class ScaffoldUtil implements Methods {

    public static double getYLevel() {
        if (!Client.moduleManager.getByClass(Speed.class).isEnabled()) {
            return mc.player.posY - 1.0;
        }
        return mc.player.posY - 1.0 >= 0.0D && Math.max(mc.player.posY, 0.0D) - Math.min(mc.player.posY, 0.0D) <= 3.0 && !mc.settings.keyBindJump.isKeyDown() ? 0.0D : mc.player.posY - 1.0;
    }

}