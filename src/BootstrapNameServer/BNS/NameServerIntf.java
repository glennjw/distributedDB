package BootstrapNameServer.BNS;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * dispatch new NS conn
 */

public class NameServerIntf implements Runnable {

    ServerSocket serverSocket;
    NameServerContainer nsContn;

    NameServerIntf(ServerSocket nportServerSkt, NameServerContainer nsContn ) {
        this.serverSocket = nportServerSkt;
        this.nsContn = nsContn;
    }

    public void run() {

        // for multi NS
        do {
            try {
                Socket nsIns = this.serverSocket.accept();
                String newConnIP = nsIns.getInetAddress().getHostAddress();
                // handle Msg or Table
                DataInputStream msgFromClient = null;
                try {
                    msgFromClient = new DataInputStream(nsIns.getInputStream());
                    String recMsg = msgFromClient.readUTF();
                    NameServerHandler nsHandler = new NameServerHandler( newConnIP, nsContn, recMsg);
                    Thread nsHandlerThr = new Thread(nsHandler);
                    nsHandlerThr.start();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                msgFromClient.close();
                nsIns.close();
            } catch (IOException exc) {
                System.out.println("NS connection failed.");
                //System.exit(1);
            }
        } while (true);                 // run once

    }

}
