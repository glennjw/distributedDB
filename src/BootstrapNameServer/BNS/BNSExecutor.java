package BootstrapNameServer.BNS;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

public class BNSExecutor implements Runnable {

    ServerSocket sktServer;
    String partiID;
    String partiIP;
    String response = "";
    Boolean ifQuit = false;
    String msgNow = "";
    Integer nPort;
    NameServerContainer nsContn;

    public BNSExecutor(ServerSocket nportServerSkt, NameServerContainer nsContn ) {
        this.sktServer = nportServerSkt;
        //this.partiIP = skt.getInetAddress().getHostAddress();
        this.nPort = nportServerSkt.getLocalPort();
        this.nsContn = nsContn;
    }

    public void run() {

        Scanner cmdRec = new Scanner(System.in);

        do {
            System.out.print("Cmd> ");
            String input = cmdRec.nextLine();        // input cmd
            if (""==input) { continue;}
            try {
                cmdInterface(input);
                System.out.println( response );
            } catch (IOException e) {
                e.printStackTrace();
            }

        } while (true);
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
            cmd = input;
        }
        // check cmd
        switch (cmd) {
            default:
                //System.out.println(cmd+" not supported.");
                break;
            case "lookup":
                cmdLookup(para);
                break;
            case "insert":
                cmdInsert(para);
                break;
            case "delete":
                cmdDelete(para);
                break;
            case "table":
                cmdTable();
                break;
            case "info":
                cmdInfo();
                break;
            case "quit":
                System.exit(0);
                break;
        }
    }

    private void cmdLookup( ArrayList<String> para ) throws IOException {
        //System.out.println("bnsexecutor: para: " + para.toString());
        Integer lookupKey = Integer.parseInt(para.get(0));
        String foundValue;

        if ( nsContn.tableHead <= lookupKey & nsContn.tableTail >= lookupKey || 0 == lookupKey ) {   // key is in this server
            foundValue = nsContn.table.lookup(lookupKey);
            foundValue = null == foundValue ? "KeyNotFound" : foundValue;
            System.out.println( foundValue );
            System.out.println(nsContn.addr.nsId.toString());
        } else {                                                                                     // key not in this server, go succ
            String informSuccMsg = "lookup " + lookupKey.toString() + " " + nsContn.addr.nsId;
            //System.out.println("send lookup msg to succ:" + informSuccMsg);
            SendMsg informSucc = new SendMsg(nsContn.successor, false, informSuccMsg);
            informSucc.send();
        }
    }


    private void cmdInsert( ArrayList<String> para ) throws IOException {
        // [ key, value, (id,id,id) ]
        Integer insertKey = Integer.parseInt(para.get(0));
        String insertValue = para.get(1);
        String tracker = "";
        for (int i = 2; i<para.size(); i++) {
            tracker += " " + para.get(i);
        }
        if ( ((nsContn.tableHead <= insertKey) && (nsContn.tableTail >= insertKey)) || 0 == insertKey ) {   // key is in this server
            nsContn.table.insert(insertKey, insertValue);
            System.out.println("Successful insert" + nsContn.addr.nsId);
        } else {                                                                                     // key not in this server, go succ
            String informSuccMsg = "insert " + insertKey.toString() + " " + insertValue + " " + tracker + " "+ nsContn.addr.nsId;
            SendMsg informSucc = new SendMsg(nsContn.successor, false, informSuccMsg);
            informSucc.send();
        }
    }

    public void cmdDelete(ArrayList<String> para) throws IOException {
        // [ key ]
        Integer deleteKey = Integer.parseInt(para.get(0));
        if ( ((nsContn.tableHead <= deleteKey) && (nsContn.tableTail >= deleteKey)) || 0 == deleteKey ) {   // key is in this server
            nsContn.table.delete(deleteKey);
            System.out.println("Successful deletion" + "\n" + nsContn.addr.nsId);
        } else {                                                                                            // key not in this server, go succ
            String informSuccMsg = "delete " + deleteKey.toString() + " "+ nsContn.addr.nsId;
            SendMsg informSucc = new SendMsg(nsContn.successor, false, informSuccMsg);
            informSucc.send();
        }
    }

    public void cmdTable( ) {
        nsContn.table.printTable();
    }

    public void cmdInfo() {
        System.out.println(   "Successful entry:\n"
                        + "Keys: " + nsContn.tableHead + "-" + nsContn.tableTail + "\n"
                        + "Predecessor: " + nsContn.predcessor.nsId + "\n"
                        + "Successor: " + nsContn.successor.nsId + "\n"
                        + "Traverse: " + nsContn.trvHis
        );
    }
}
