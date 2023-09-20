package wtf.tophat.events.impl;

import net.minecraft.entity.Entity;
import wtf.tophat.events.Event;

public class EntityRendererEvent extends Event {
    private Entity entity;

    public EntityRendererEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}