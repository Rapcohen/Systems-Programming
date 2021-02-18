package bgu.spl.net.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.srv.BGRSProtocol;

public class ERR extends Message {
    private final short msgOpcode;

    public ERR(short msgOpcode) {
        super(Short.parseShort("13"));
        this.msgOpcode = msgOpcode;
    }

    @Override
    public Message execute(BGRSProtocol protocol) {
        return null;
    }

    @Override
    public String toString() {
        return Short.toString(opcode) + "\0" + Short.toString(msgOpcode);
    }
}
