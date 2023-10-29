package wtf.tophat.utilities.player;

import wtf.tophat.utilities.Methods;

public class PlayerUtil implements Methods {

    public static boolean isMathGround() {
        return mc.player.posY % 0.015625 == 0;
    }

}
