package NameServer.NS;

import NameServer.NS.NameServerAddr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SendMsg {
    NameServerAddr ns;
    String msg;
    Boolean needReply = false;

    SendMsg(NameServerAddr addr, Boolean needReply, String msg) {
        this.ns = addr;
        this.msg = msg;
        this.needReply = needReply;
    }

    public String send () throws IOException {
        String response;
        DataInputStream recNportSkt = null;
        DataOutputStream sendNportSkt = null;
        Socket nportSkt = null;
        //System.out.println("send: "+msg);
        try {
            nportSkt = new Socket(ns.IP, ns.port);
            recNportSkt = new DataInputStream(nportSkt.getInputStream());
            sendNportSkt = new DataOutputStream(nportSkt.getOutputStream());
        } catch (IOException eIO) {
            System.out.println("Connect NS: " + ns.IP + " " + ns.port + " failed.");
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
