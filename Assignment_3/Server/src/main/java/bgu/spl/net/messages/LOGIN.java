package bgu.spl.net.messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.srv.BGRSProtocol;

public class LOGIN extends Message {
    private final String username;
    private final String password;

    public LOGIN(String username, String password) {
        super(Short.parseShort("3"));
        this.username = username;
        this.password = password;
    }

    @Override
    public Message execute(BGRSProtocol protocol) {
        // if user is already logged in - the protocol returns an error
        if (protocol.getUser() == null) {
            boolean loginSuccessful = db.login(username, password);
            if (loginSuccessful) {
                protocol.setUser(db.getUser(username));
                return new ACK("", opcode);
            }
        }
        return new ERR(opcode);
    }
}
