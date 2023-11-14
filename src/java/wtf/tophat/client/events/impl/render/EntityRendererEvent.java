package wtf.tophat.client.events.impl.render;

import net.minecraft.entity.Entity;
import wtf.tophat.client.events.base.Event;

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