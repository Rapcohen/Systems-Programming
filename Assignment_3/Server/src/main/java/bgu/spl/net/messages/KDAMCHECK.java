package bgu.spl.net.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.srv.BGRSProtocol;

import java.util.List;

public class KDAMCHECK extends Message {
    private final short courseNum;

    public KDAMCHECK(short courseNum) {
        super(Short.parseShort("6"));
        this.courseNum = courseNum;
    }

    @Override
    public Message execute(BGRSProtocol protocol) {
        if (protocol.getUser() != null) {
            List<Short> kdams = db.getKdamCourses(courseNum);
            if (kdams != null)
                return new ACK(kdams.toString(), opcode);
        }
        return new ERR(opcode);
    }
}
