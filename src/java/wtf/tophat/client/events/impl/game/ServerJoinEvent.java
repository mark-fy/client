package wtf.tophat.client.events.impl.game;

import wtf.tophat.client.events.base.Event;

public class ServerJoinEvent extends Event {

    private String ip;
    private int port;

    public ServerJoinEvent(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
