package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
	int serialNumber;
    boolean available;


	public Ewok(int serialNumber){
	    this.serialNumber = serialNumber;
	    this.available = true;
    }
  
    /**
     * Acquires an Ewok
     * @PRE: available == true.
     * @POST: available == false.
     *
     */
    public synchronized void acquire() {
        while (!isAvailable()){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        changeAvailable();
    }

    /**
     * release an Ewok
     * @PRE:
     *    available == false.
     * @POST:
     *    available == true.
     */
    public synchronized void release() {
        if (!isAvailable()){
            changeAvailable();
            this.notifyAll();
        }
    }

    /**
     * @return available
     */
    public boolean isAvailable() { // TODO check if can be private
        return available;
    }

    private void changeAvailable(){
        this.available = !this.available;
    }
}
