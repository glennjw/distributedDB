package NameServer.NS;

public class NameServerAddr {
    String IP;
    Integer port;
    Integer nsId;

    NameServerAddr(String IP, Integer port) {
        this.IP = IP;
        this.port = port;
    }


    NameServerAddr(String IP, Integer port, Integer nsId) {
        this.IP = IP;
        this.port = port;
        this.nsId = nsId;
    }
}
