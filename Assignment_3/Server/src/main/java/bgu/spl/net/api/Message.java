package bgu.spl.net.api;

import bgu.spl.net.Database;
import bgu.spl.net.srv.BGRSProtocol;
/**
 * Abstract Class which represents a message that appears in the communication
 * between the Server and Client in the 'BGRS'.
 * <p>Each derived class is required to implement the {@code execute(BGRSProtocol protocol)}
 * abstract method as described in the 'BGRS' specifications.
 */
public abstract class Message {
    protected short opcode;
    protected Database db = Database.getInstance();

    public Message(short opcode) {
        this.opcode = opcode;
    }

    public abstract Message execute(BGRSProtocol protocol);

    public short getOpcode() {
        return opcode;
    }
}
