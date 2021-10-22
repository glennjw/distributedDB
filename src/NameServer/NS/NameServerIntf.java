package NameServer.NS;

import NameServer.NS.NameServerContainer;
import NameServer.NS.NameServerHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
        do {
            try {
                DataInputStream msgRecv = null;
                String recMsg;
                Socket nsIns = this.serverSocket.accept();
                // handle Msg or Table
                try {
                    msgRecv = new DataInputStream(nsIns.getInputStream());
                    recMsg = msgRecv.readUTF();
                    String newConnIP = nsIns.getInetAddress().getHostAddress();
                    NameServerHandler nsHandler = new NameServerHandler( newConnIP, nsContn, recMsg );
                    Thread nsHandlerThr = new Thread(nsHandler);
                    nsHandlerThr.start();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                msgRecv.close();
                nsIns.close();
            } catch (IOException exc) {
                System.out.println("NS connection failed.");
                //System.exit(1);
            }
        } while (true);                 // run once

    }

}
