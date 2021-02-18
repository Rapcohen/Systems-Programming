package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class BombDestroyerEvent implements Event<Boolean> { // For now this is a Boolean Event
    private long duration;

    /**
     * constructor
     * @param duration
     */
    public BombDestroyerEvent(long duration) {
        this.duration = duration;
    }

    /**
     * getter
     * @return duration.
     */
    public long getDuration() {
        return duration;
    }
}
