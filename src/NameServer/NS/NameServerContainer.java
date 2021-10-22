package NameServer.NS;

import NameServer.NS.NameServerAddr;
import NameServer.NS.NameTable;

import java.util.ArrayList;
import java.util.List;

public class NameServerContainer {
    Integer tableHead;
    Integer tableTail;
    NameServerAddr addr;
    NameServerAddr bns = null;
    NameServerAddr predcessor = null;
    NameServerAddr successor = null;
    NameTable table;
    String trvHis;

    NameServerContainer(Integer nsId, String IP, Integer port) {
        this.addr = new NameServerAddr(IP,port,nsId);
        this.tableTail = addr.nsId;
    }

    NameServerContainer(Integer nsId, String IP, Integer port, Integer tableHead) {
        this.addr = new NameServerAddr(IP,port,nsId);
        this.tableTail = addr.nsId;
        this.tableHead = tableHead;
    }

    public void createTable ( ArrayList<String> table) {
        this.table = new NameTable(table);
    }

    public void setPredcessor (NameServerAddr newPredc) {
        predcessor = newPredc;
        tableHead = newPredc.nsId +1;
    }

    public void setSuccessor (NameServerAddr newSucc) {
        successor = newSucc;
        tableTail = addr.nsId;
    }
}
