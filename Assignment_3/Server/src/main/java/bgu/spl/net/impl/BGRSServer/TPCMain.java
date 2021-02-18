package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.BGRSEncDec;
import bgu.spl.net.srv.BGRSProtocol;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Missing arguments");
            System.exit(1);
        }

        Server.threadPerClient(
                Integer.parseInt(args[0]),
                BGRSProtocol::new,
                BGRSEncDec::new
        ).serve();
    }
}
