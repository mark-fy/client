package net.minecraft.util;

import net.minecraft.client.Minecraft;
import wtf.tophat.events.impl.TimeEvent;

public class Timer {
    public int elapsedTicks;
    public float partialTicks;
    public float elapsedPartialTicks;
    public float renderPartialTicks;
    private long lastSyncSysClock;
    private float tickLength;
    public float timerSpeed;

    public Timer(float tps) {
        this.tickLength = 1000.0F / tps;
        this.lastSyncSysClock = Minecraft.getSystemTime();
        this.timerSpeed = 1.0F;
    }

    public void updateTimer() {
        TimeEvent eventTime = new TimeEvent(Minecraft.getSystemTime());
        eventTime.call();
        long i = eventTime.getBalance();
        this.elapsedPartialTicks = (float)(i - this.lastSyncSysClock) / this.tickLength * this.timerSpeed;
        this.lastSyncSysClock = i;
        this.partialTicks += this.elapsedPartialTicks;
        this.elapsedTicks = (int)this.partialTicks;
        this.partialTicks -= (float)this.elapsedTicks;
        this.renderPartialTicks = this.partialTicks;
    }
}
