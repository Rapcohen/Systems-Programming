package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;


public class DeactivationEvent implements Event<Boolean> {
    private long duration;

    /**
     * constructor
     * @param duration
     */
    public DeactivationEvent(long duration) {
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
