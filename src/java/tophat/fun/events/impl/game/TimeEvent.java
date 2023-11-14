package tophat.fun.events.impl.game;

import tophat.fun.events.Event;

public class TimeEvent extends Event {

    private long balance = 0;

    public TimeEvent(long balance) {
        this.balance = balance;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void decrementBalance(long amount) {
        balance -= amount;
    }

}
