package bgu.spl.net.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.srv.BGRSProtocol;

public class ISREGISTERED extends Message {
    private final short courseNum;

    public ISREGISTERED(short courseNum) {
        super(Short.parseShort("9"));
        this.courseNum = courseNum;
    }

    @Override
    public Message execute(BGRSProtocol protocol) {
        if (protocol.getUser() != null && !protocol.getUser().isAdmin()) {
            Boolean isRegistered = db.isRegistered(courseNum, protocol.getUser());
            if (isRegistered != null) {
                String output = isRegistered ? "REGISTERED" : "NOT REGISTERED";
                return new ACK(output, opcode);
            }
        }
        return new ERR(opcode);
    }
}
