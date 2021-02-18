package bgu.spl.net.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.srv.BGRSProtocol;

public class COURSESTAT extends Message {
    private final short courseNum;

    public COURSESTAT(short courseNum) {
        super(Short.parseShort("7"));
        this.courseNum = courseNum;
    }

    @Override
    public Message execute(BGRSProtocol protocol) {
        if (protocol.getUser() != null && protocol.getUser().isAdmin()) {
            String courseStat = db.getCourseStat(courseNum);
            if (courseStat != null) {
                return new ACK(courseStat, opcode);
            }
        }
        return new ERR(opcode);
    }
}
