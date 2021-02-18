package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.MicroService;
import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    private AtomicInteger totalAttacks = new AtomicInteger(0);
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    private static class singletonHolder {
        private static final Diary instance = new Diary();
    }

    private Diary() {}

    /**
     * get instance of diary singleton
     * @return dairy singleton
     */

    public static Diary getInstance() {
        return singletonHolder.instance;
    }

    /**
     * setFinishTime is a method used by a microservice to stamp in the diary instance the time it finished its
     * role in the battle.
     * @param m microservice who calls the method.
     * @param timeStamp the current time to stamp in the diary.
     */

    public void setFinishTime(MicroService m, long timeStamp) {
        switch (m.getName()) {
            case "Han":
                HanSoloFinish = timeStamp;
                break;
            case "C3PO":
                C3POFinish = timeStamp;
                break;
            case "R2D2":
                R2D2Deactivate = timeStamp;
                break;
        }
    }

    /**
     * setTerminateTime is a method used by a microservice to stamp in the diary instance the time
     * it finished its 'run' method.
     * @param m microservice who calls the method.
     * @param timeStamp the current time to stamp in the diary.
     */

    public void setTerminateTime(MicroService m, long timeStamp) {
        switch (m.getName()) {
            case "Leia":
                LeiaTerminate = timeStamp;
                break;
            case "Han":
                HanSoloTerminate = timeStamp;
                break;
            case "C3PO":
                C3POTerminate = timeStamp;
                break;
            case "R2D2":
                R2D2Terminate = timeStamp;
                break;
            case "Lando":
                LandoTerminate = timeStamp;
                break;
        }
    }

    /**
     * used only by Han or C3PO, incTotalAttacks is used to increment the amount of total attacks - which is preformed atomically.
     * @param m microservice- Han or CP3O.
     */
    public void incTotalAttacks(MicroService m) {
        if (m.getName().equals("Han") || m.getName().equals("C3PO") ){
            totalAttacks.incrementAndGet();
        }
    }

    /**
     * creats an output file in a json format.
     * @param path the path to the output file.
     */

    public void createOutputFile(String path) {
        Gson gson = new Gson();
        try{
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(gson.toJson(this));
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
