package BootstrapNameServer.BNS;

import java.io.IOException;

class BNS {

    public static void main(String[] args) throws IOException {
        String config = args[0];
        SingleBootstrapNameServer BNS = new SingleBootstrapNameServer( config );
        System.out.println("******* BNS running ******");
        BNS.run();
    }

}

