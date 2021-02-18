package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

public class AttackEvent implements Event<Boolean> {
	private Attack attack;

    /**
     * constructor
     * @param attack
     */
	public AttackEvent(Attack attack) {
	    this.attack = attack;
    }

    /**
     * getter for attack field
     * @return attack.
     */
    public Attack getAttack() {
        return attack;
    }
}
