package bgu.spl.mics.application.services;

import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private Diary diary = Diary.getInstance();

    public R2D2Microservice() {
        super("R2D2");
    }

    @Override
    protected void initialize() {
        subscribeEvent(DeactivationEvent.class, (DeactivationEvent event) -> {
            try {
                Thread.sleep(event.getDuration());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            complete(event, true);
            diary.setFinishTime(this, System.currentTimeMillis());
        });

        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast broadcast) -> {
            terminate();
            diary.setTerminateTime(this,System.currentTimeMillis());
            Main.terminationDoneSignal.countDown();
        });
        Main.initializeDoneSignal.countDown();
    }
}
