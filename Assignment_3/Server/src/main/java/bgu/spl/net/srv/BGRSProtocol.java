package bgu.spl.net.srv;

import bgu.spl.net.User;
import bgu.spl.net.api.Message;
import bgu.spl.net.api.MessagingProtocol;

public class BGRSProtocol implements MessagingProtocol<Message> {
    private User user = null;
    private boolean shouldTerminate = false;

    @Override
    public Message process(Message msg) {
        return msg.execute(this);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public void setShouldTerminate(boolean shouldTerminate) {
        this.shouldTerminate = shouldTerminate;
    }
}