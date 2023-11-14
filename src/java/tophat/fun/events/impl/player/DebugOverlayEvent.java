package tophat.fun.events.impl.player;

import tophat.fun.events.Event;

public class DebugOverlayEvent extends Event {

    private String name, version;

    public DebugOverlayEvent(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
