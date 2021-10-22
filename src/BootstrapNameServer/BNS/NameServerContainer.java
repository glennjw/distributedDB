package BootstrapNameServer.BNS;

import java.util.ArrayList;
import java.util.List;

public class NameServerContainer {
    //Integer nsId;
    Integer tableHead;
    Integer tableTail;
    NameServerAddr addr;
    NameServerAddr predcessor = null;
    NameServerAddr successor = null;
    NameTable table;
    String trvHis;

    NameServerContainer(NameServerAddr addr, Integer tableHead, Integer tableTail) {
        //this.nsId = nsId;
        this.tableHead = tableHead;
        this.tableTail = tableTail;
        this.addr = addr;
    }

    NameServerContainer(Integer nsId, String IP, Integer port, Integer tableTail) {
        //this.nsId = nsId;
        this.tableTail = tableTail;
        this.addr = new NameServerAddr(IP,port, nsId);
    }

    public void createTable ( ArrayList<String> table) {
        this.table = new NameTable( table);
    }

    public void setPredcessor (NameServerAddr newPredc) {
        predcessor = newPredc;
        tableHead = newPredc.nsId +1;
    }

    public void setSuccessor (NameServerAddr newSucc) {
        successor = newSucc;
        if (0==addr.nsId) {return;}
        tableTail = addr.nsId;
    }


}
