package BootstrapNameServer.BNS;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SendMsg {
    NameServerAddr nsAddr;
    String msg;
    Boolean needReply = false;

    SendMsg(NameServerAddr addr, Boolean needReply, String msg) {
        this.nsAddr = addr;
        this.msg = msg;
        this.needReply = needReply;
    }

    public String send () throws IOException {
        String response = null;
        DataInputStream recNportSkt = null;
        DataOutputStream sendNportSkt = null;
        Socket nportSkt = null;
        //System.out.println("send: "+ msg + " to " + nsAddr.IP);
        try {
            nportSkt = new Socket(nsAddr.IP, nsAddr.port);
            recNportSkt = new DataInputStream(nportSkt.getInputStream());
            sendNportSkt = new DataOutputStream(nportSkt.getOutputStream());
        } catch (IOException eIO) {
            System.out.println("Connect NS: " + nsAddr.IP + " " + nsAddr.port + " failed.");
            eIO.printStackTrace();
            //System.exit(0);
        }
        sendNportSkt.writeUTF(msg);
        response = needReply ? recNportSkt.readUTF() : null;
        recNportSkt.close();
        sendNportSkt.close();
        nportSkt.close();
        return response;

    }
}
