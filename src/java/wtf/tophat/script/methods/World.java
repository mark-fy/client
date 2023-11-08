package wtf.tophat.script.methods;

import wtf.tophat.client.utilities.Methods;

public class World implements Methods {

    // Setters

    public void setGameSpeed(float speed) { getMCTimer().timerSpeed = speed; }

    // Getters

    public float getGameSpeed() { return getMCTimer().timerSpeed; }
    public String getDate() { return Methods.getCurrentDate(); }

}
