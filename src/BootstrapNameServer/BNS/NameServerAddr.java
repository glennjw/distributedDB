package BootstrapNameServer.BNS;

public class NameServerAddr {
    Integer nsId;
    String IP;
    Integer port;


    NameServerAddr (String IP, Integer port) {
        this.IP = IP;
        this.port = port;
    }



    NameServerAddr (String IP, Integer port, Integer nsId) {
        this.IP = IP;
        this.port = port;
        this.nsId = nsId;
    }

}
