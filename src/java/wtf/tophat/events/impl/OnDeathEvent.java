package wtf.tophat.events.impl;

import net.minecraft.entity.player.EntityPlayer;
import wtf.tophat.events.base.Event;

public class OnDeathEvent extends Event {

    private EntityPlayer player;

    public OnDeathEvent(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}
