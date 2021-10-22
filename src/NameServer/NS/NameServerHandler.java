package NameServer.NS;

import NameServer.NS.SendMsg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * handle new conn from new NS
 */

public class NameServerHandler implements Runnable {
    String connIP;
    NameServerContainer nsContn;
    String recMsg;

    NameServerHandler(String connIP, NameServerContainer nsContn, String recMsg) {
        this.connIP = connIP;
        this.nsContn = nsContn;
        this.recMsg = recMsg;
    }

    public void run() {
        try {
            cmdInterface( recMsg );
        } catch (IOException e) {
            System.out.println(recMsg + ": processing failed");
            //e.printStackTrace();
        }
    }

    private void cmdInterface(String input) throws IOException {
        String cmd = "";
        ArrayList<String> para = new ArrayList<>();
        // split and assign input to cmd and para
        input = input.trim();
        if (-1 < input.indexOf(" ")) {
            String[] inputSplited = input.split(" ");
            cmd = inputSplited[0].toLowerCase();
            for (int i=1; i<inputSplited.length; i++) { para.add(inputSplited[i]);}
        } else {
            cmd = input.toLowerCase();
        }
        // check cmd
        switch (cmd) {
            default:
                System.out.println(cmd + " not supported NS.");
                break;
            case "newpredcessor":
                cmdNewPredcessor(para);
                break;
            case "addtable":
                cmdAddtable(para);
                break;
            case "newsuccessor":
                cmdNewSuccessor(para);
                break;
            case "addnewns":
                cmdAddNewNS(para);
                break;
            case "trvhis":
                cmdTrvHis(para);
                break;
            case "lookup":
                cmdLookup(para);
                break;
            case "lookuprst":
                cmdLookupRst(para);
                break;
            case "insert":
                cmdInsert(para);
                break;
            case "insertrst":
                cmdInsertRst(para);
                break;
            case "delete":
                cmdDelete(para);
                break;
            case "deleterst":
                cmdDeleteRst(para);
                break;
        }
    }

    public void cmdLookup (ArrayList<String> para) throws IOException {
        // [ key, [id,id,id] ]
        Integer lookupKey = Integer.parseInt(para.get(0));
        String tracker = "";
        for (int i = 1; i<para.size(); i++) {
            tracker += " " + para.get(i);
        }
        String foundValue;
        if ( nsContn.tableHead <= lookupKey & nsContn.tableTail >= lookupKey ) {   // key is in this server
            foundValue = nsContn.table.lookup(lookupKey);
            foundValue = null == foundValue ? "KeyNotFound" : foundValue;
            String informSuccMsg = "lookupRst " + foundValue + " " + tracker + " " + nsContn.addr.nsId;
            SendMsg informSucc = new SendMsg(nsContn.bns, false, informSuccMsg);
            informSucc.send();
        } else {                                                                                     // key not in this server, go succ
            String informSuccMsg = "lookup " + lookupKey.toString() + " " + tracker + " " + nsContn.addr.nsId;
            SendMsg informSucc = new SendMsg(nsContn.successor, false, informSuccMsg);
            informSucc.send();
        }
    }

    public void cmdLookupRst(ArrayList<String> para) {
        // [ Cabbage 0 100 ]
        System.out.println(para.get(0) + "\n" + para.subList(1,para.size()));
    }

    private void cmdInsert( ArrayList<String> para ) throws IOException {
        // [ key, value, (id,id,id) ]
        Integer insertKey = Integer.parseInt(para.get(0));
        String insertValue = para.get(1);
        String tracker = "";
        for (int i = 2; i<para.size(); i++) {
            tracker += (" " + para.get(i));
        }
        if ( ((nsContn.tableHead <= insertKey) && (nsContn.tableTail >= insertKey)) || 0 == insertKey ) {   // key is in this server
            nsContn.table.insert(insertKey, insertValue);
            String informSuccMsg = "insertRst " + "Successful insert" + " " + tracker + " " + nsContn.addr.nsId;
            SendMsg informSucc = new SendMsg(nsContn.bns, false, informSuccMsg);
            informSucc.send();
        } else {                                                                                     // key not in this server, go succ
            String informSuccMsg = "insert " + insertKey.toString() + " " + insertValue + " " + tracker + " "+ nsContn.addr.nsId;
            System.out.println("send msg from NShandler: " + informSuccMsg);
            SendMsg informSucc = new SendMsg(nsContn.successor, false, informSuccMsg);
            informSucc.send();
        }
    }

    public void cmdInsertRst (ArrayList<String> para) {
        System.out.println(para.get(0) + "\n" + para.subList(1,para.size()));
    }

    public void cmdDelete(ArrayList<String> para) throws IOException {
        System.out.println("delete recvd: "+ para.toString());
        // [ key, (id,id,id) ]
        String tracker = "";
        for (int i = 1; i<para.size(); i++) {
            tracker += " " + para.get(i);
        }
        Integer deleteKey = Integer.parseInt(para.get(0));
        if ( ((nsContn.tableHead <= deleteKey) && (nsContn.tableTail >= deleteKey)) || 0 == deleteKey ) {   // key is in this server
            String deleteRst = nsContn.table.delete(deleteKey);
            String informSuccMsg = "deleteRst " + deleteRst + " " + tracker + " " + nsContn.addr.nsId;
            SendMsg informSucc = new SendMsg(nsContn.bns, false, informSuccMsg);
            informSucc.send();
        } else {                                                                                            // key not in this server, go succ
            String informSuccMsg = "delete " + deleteKey.toString() + " "+  tracker + " "+nsContn.addr.nsId;
            SendMsg informSucc = new SendMsg(nsContn.successor, false, informSuccMsg);
            informSucc.send();
        }
    }

    public void cmdDeleteRst (ArrayList<String> para) {
        String rst = "";
        for (int i=1; i<para.size(); i++) { rst +=  " " + para.get(i); }
        System.out.println( para.get(0) + "\n" + rst);
    }

    public void cmdTrvHis (ArrayList<String> para) {
        nsContn.trvHis = para.toString().replace(",", "").replace("[", "").replace("]", "");

    }
    public void cmdNewPredcessor (ArrayList<String> para) throws IOException {
        // [ port, nsID ] / [ IP, port, nsID ]
        NameServerAddr predc = 2==para.size() ? new NameServerAddr( connIP, Integer.parseInt(para.get(0)), Integer.parseInt(para.get(1)) )  : new NameServerAddr( para.get(0), Integer.parseInt(para.get(1)), Integer.parseInt(para.get(2)) );
        nsContn.setPredcessor( predc );
    }

    public void cmdNewSuccessor (ArrayList<String> para) throws IOException {
        // [ port, nsID ] /  [ IP, port, nsID ]
        NameServerAddr succ = 2==para.size() ? new NameServerAddr( connIP, Integer.parseInt(para.get(0)), Integer.parseInt(para.get(1)) ) : new NameServerAddr( para.get(0), Integer.parseInt(para.get(1)), Integer.parseInt(para.get(2)) );
        nsContn.setSuccessor( succ );
    }

    public void cmdAddtable (ArrayList<String> para) {
        nsContn.table.append( para );
    }

    public void cmdAddNewNS (ArrayList<String> recMsg) throws IOException {
        // recMsg = [ ID, IP, port, (id,id,id) ]
        NameServerAddr newNS = new NameServerAddr( recMsg.get(1), Integer.parseInt((String) recMsg.get(2)), Integer.parseInt((String) recMsg.get(0) ));
        //if ( null == nsContn.successor && newNSId > nsContn.nsId ) {      // add 1st newNS to successor
        if ( nsContn.tableHead <= newNS.nsId && newNS.nsId <= nsContn.tableTail ) {     // newns insert into BNS
            /**
            String informPredMsg = "newPredcessor " + nsContn.predcessor.port + " " + nsContn.predcessor.nsId;  // inform newNS's predecessor
            SendMsg informPred = new SendMsg(newNS, false, informPredMsg);
            informPred.send();
            String informSuccMsg = "newSuccessor " + nsContn.addr.port + " " + nsContn.addr.nsId;  // inform newNS's successor for next newNS
            SendMsg informSucc = new SendMsg(newNS, false, informSuccMsg);
            informSucc.send();
            List<String> newNSTable = nsContn.table.slice(nsContn.predcessor.nsId+1, newNS.nsId);    // split table
            String addTableMsg = "addTable " + Arrays.toString(newNSTable.toArray()).replace(",", "").replace("[", "").replace("]", "");
            SendMsg sendTable = new SendMsg(newNS, false, addTableMsg);
            sendTable.send();

            String informOldPredSuccMsg = "newSuccessor " + newNS.IP + " " + nsContn.addr.port + " " + nsContn.addr.nsId;
            SendMsg informOldPredSucc = new SendMsg(nsContn.predcessor, false, informOldPredSuccMsg);
            informOldPredSucc.send();

            nsContn.setPredcessor( newNS );
            if (null==nsContn.successor) { nsContn.successor = newNS; }           // for 1st ns to BNS

            nsContn.setPredcessor( newNS );

            // send traverse history
            if (recMsg.size()>3) {
                recMsg.remove(0);
                recMsg.remove(0);
                recMsg.remove(0);
            }
            String trvHisMsg = "trvHis " + recMsg.toString().replace(",", "").replace("[", "").replace("]", "") + " " + nsContn.addr.nsId;
            SendMsg trvHisSenf = new SendMsg(newNS, false, trvHisMsg);
            trvHisSenf.send();
             */


            if (null==nsContn.successor) { nsContn.successor = newNS; }

            String informPredMsg = "newPredcessor " + nsContn.predcessor.port + " " + nsContn.predcessor.nsId;  // inform newNS's predecessor
            SendMsg informPred = new SendMsg(newNS, false, informPredMsg);
            informPred.send();
            // tell ori predec, newNS is succ
            String informOldPredSuccMsg = "newSuccessor " + newNS.IP + " " + newNS.port + " " + newNS.nsId;
            // String informOldPredSuccMsg = "newSuccessor " + newNS.IP + " " + nsContn.addr.port + " " + nsContn.addr.nsId;
            SendMsg informOldPredSucc = new SendMsg(nsContn.predcessor, false, informOldPredSuccMsg);
            informOldPredSucc.send();

            // no need to set succ for the last ns, prevent cycle
            String informSuccMsg = "newSuccessor " + nsContn.addr.port + " " + nsContn.addr.nsId;  // inform newNS's successor for next newNS
            SendMsg informSucc = new SendMsg(newNS, false, informSuccMsg);
            informSucc.send();

            // add table to newNS
            List<String> newNSTable = nsContn.table.slice(nsContn.predcessor.nsId+1, newNS.nsId);    // split table
            String addTableMsg = "addTable " + Arrays.toString(newNSTable.toArray()).replace(",", "").replace("[", "").replace("]", "");
            SendMsg sendTable = new SendMsg(newNS, false, addTableMsg);
            sendTable.send();

            nsContn.setPredcessor( newNS );

            // send traverse history
            String trvHisMsg = "trvHis " + recMsg.toString().replace(",", "").replace("[", "").replace("]", "") + " " + nsContn.addr.nsId;
            SendMsg trvHisSenf = new SendMsg(newNS, false, trvHisMsg);
            trvHisSenf.send();

        } else if ( newNS.nsId > nsContn.addr.nsId ) {                            // if 2nd or hier NS, pass to next NS
            // send new NS info to next NS: successor
            String formatedMsg = "addNewNS " + recMsg.toString().replace(",", "").replace("[", "").replace("]", "") + " " + nsContn.addr.nsId;    // the last is for traverse.
            //System.out.println("trahis: " + formatedMsg);
            SendMsg sendMsg = new SendMsg( nsContn.successor, false , formatedMsg);
            sendMsg.send();
        } else {
            System.out.println("new NS Id: " + newNS.nsId + " is smaller than " + nsContn.addr.nsId + "'s head: " + nsContn.tableHead);
        }
    }

}
