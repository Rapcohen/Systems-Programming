package bgu.spl.net.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.srv.BGRSProtocol;

public class UNREGISTER extends Message {
    private final short courseNum;

    public UNREGISTER(short courseNum) {
        super(Short.parseShort("10"));
        this.courseNum = courseNum;
    }

    @Override
    public Message execute(BGRSProtocol protocol) {
        if (protocol.getUser() != null && !protocol.getUser().isAdmin()) {
            boolean unReg = db.unReg(courseNum, protocol.getUser());
            if (unReg) {
                return new ACK("", opcode);
            }
        }
        return new ERR(opcode);
    }
}
