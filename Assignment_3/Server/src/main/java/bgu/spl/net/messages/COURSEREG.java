package bgu.spl.net.messages;

import bgu.spl.net.User;
import bgu.spl.net.api.Message;
import bgu.spl.net.srv.BGRSProtocol;

public class COURSEREG extends Message {
    private final short courseNum;

    public COURSEREG(short courseNum) {
        super(Short.parseShort("5"));
        this.courseNum = courseNum;
    }

    @Override
    public Message execute(BGRSProtocol protocol) {
        User user = protocol.getUser();
        if (user != null && db.courseReg(courseNum, user)) {
            return new ACK("", opcode);
        }
        return new ERR(opcode);
    }
}
