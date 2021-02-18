package bgu.spl.net.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.srv.BGRSProtocol;

public class MYCOURSES extends Message {
    public MYCOURSES() {
        super(Short.parseShort("11"));
    }

    @Override
    public Message execute(BGRSProtocol protocol) {
        if (protocol.getUser() != null && !protocol.getUser().isAdmin()) {
            String myCoursesList = protocol.getUser().coursesToString();
            return new ACK(myCoursesList, opcode);
        }
        return new ERR(opcode);
    }
}
