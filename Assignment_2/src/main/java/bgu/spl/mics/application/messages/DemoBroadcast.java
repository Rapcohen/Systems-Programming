package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class DemoBroadcast implements Broadcast {
    public void message(){
        System.out.println("Im a broadcast message!");
    }
}
