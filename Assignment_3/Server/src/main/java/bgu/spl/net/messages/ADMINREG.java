package bgu.spl.net.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.srv.BGRSProtocol;

public class ADMINREG extends Message {
    private final String username;
    private final String password;

    public ADMINREG(String username, String password) {
        super(Short.parseShort("1"));
        this.username = username;
        this.password = password;
    }

    @Override
    public Message execute(BGRSProtocol protocol) {
        // user must not be logged in - in order to preform Registration
        if (protocol.getUser() == null && db.addUser(username, password, true)) {
            return new ACK("", opcode);
        } else
            return new ERR(opcode);
    }
}
