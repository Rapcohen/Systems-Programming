package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import java.util.ArrayList;
import java.util.List;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Diary diary = Diary.getInstance();
	private long deactivateDuration;
	private long bombDuration;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
    }

    @Override
    protected void initialize() {
        subscribeEvent(BattleEvent.class,(BattleEvent event)->{
            try {
                Main.initializeDoneSignal.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<Future<Boolean>> attackFutures = new ArrayList<>(attacks.length);
            for(Attack attack : attacks){
                Future<Boolean> future = sendEvent(new AttackEvent(attack));
                attackFutures.add(future);
            }
            for (Future<Boolean> future : attackFutures){
                future.get();// get is blocking until future is resolved.
            }
            Future<Boolean> deactivate = sendEvent(new DeactivationEvent(deactivateDuration));
            deactivate.get();// get is blocking until future is resolved.
            Future<Boolean> bomb = sendEvent(new BombDestroyerEvent(bombDuration));
            bomb.get();// get is blocking until future is resolved.
            sendBroadcast(new TerminateBroadcast());
        });
        subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast b)->{
            terminate();
            diary.setTerminateTime(this,System.currentTimeMillis());
            Main.terminationDoneSignal.countDown();
        });
        sendEvent(new BattleEvent());
    }

    /**
     * setter
     * @param deactivateDuration
     */
    public void setDeactivateDuration(long deactivateDuration) {
        this.deactivateDuration = deactivateDuration;
    }

    /**
     * setter
     * @param bombDuration
     */
    public void setBombDuration(long bombDuration) {
        this.bombDuration = bombDuration;
    }
}
