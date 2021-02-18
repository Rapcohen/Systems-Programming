package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DemoBroadcast;
import bgu.spl.mics.application.messages.DemoEvent;

public class TestMicroservice extends MicroService {
    public TestMicroservice (){
        super("Test");
    }

    @Override
    protected void initialize() {
        subscribeEvent(DemoEvent.class, (DemoEvent e) -> System.out.println("Event Message"));
        subscribeBroadcast(DemoBroadcast.class,(DemoBroadcast a)-> System.out.println("Broadcast Message"));
    }
}
