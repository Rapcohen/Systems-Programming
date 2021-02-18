package bgu.spl.net.messages;

import bgu.spl.net.User;
import bgu.spl.net.api.Message;
import bgu.spl.net.srv.BGRSProtocol;

public class LOGOUT extends Message {

    public LOGOUT() {
        super(Short.parseShort("4"));
    }

    @Override
    public Message execute(BGRSProtocol protocol) {
        User user = protocol.getUser();
        if (user != null) {
            db.logout(user.getUsername());
            protocol.setShouldTerminate(true);
            return new ACK("", opcode);
        }
        return new ERR(opcode);
    }
}
