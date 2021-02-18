package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.BGRSEncDec;
import bgu.spl.net.srv.BGRSProtocol;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Missing arguments");
            System.exit(1);
        }

        Server.reactor(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[0]),
                BGRSProtocol::new,
                BGRSEncDec::new
        ).serve();
    }
}
