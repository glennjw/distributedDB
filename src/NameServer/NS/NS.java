package NameServer.NS;

import java.io.IOException;

class NS {

    public static void main(String[] args) throws IOException {
        String config = args[0];
        SingleNameServer BNS = new SingleNameServer( config );
        System.out.println("******* NS running ******");
        BNS.run();
    }

}

