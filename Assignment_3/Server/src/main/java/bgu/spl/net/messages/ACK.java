package bgu.spl.net.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.srv.BGRSProtocol;

public class ACK extends Message {
    private final String attachment;
    private final short msgOpcode;

    public ACK(String attachment, short msgOpcode) {
        super(Short.parseShort("12"));
        this.attachment = attachment;
        this.msgOpcode = msgOpcode;
    }

    @Override
    public Message execute(BGRSProtocol protocol) {
        return null;
    }

    @Override
    /*public String toString() {
        return  msgOpcode +"\0"+ attachment ;

    }*/
    public String toString() {
        return Short.toString(opcode) + "\0" + Short.toString(msgOpcode) + "\0" + attachment;
    }
}
