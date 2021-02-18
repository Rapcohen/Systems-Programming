package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class DemoEvent implements Event<Boolean> {
    public void message(){
        System.out.println("Im an event message!");
    }

}
