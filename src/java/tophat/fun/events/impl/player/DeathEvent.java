package tophat.fun.events.impl.player;

import net.minecraft.entity.player.EntityPlayer;
import tophat.fun.events.Event;

public class DeathEvent extends Event {

    private EntityPlayer player;

    public DeathEvent(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

}
