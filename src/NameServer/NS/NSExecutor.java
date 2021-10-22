package NameServer.NS;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NSExecutor implements Runnable {

    ServerSocket sktServer;
    String response = null;
    Integer nPort;
    NameServerContainer nsContn;
    Boolean keepUp = true;

    public NSExecutor(ServerSocket nportServerSkt, NameServerContainer nsContn ) {
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
                if (null!=response) {System.out.println( response );}
            } catch (IOException | InterruptedException e) {
                System.out.println("Cmd failed.");
            }

        } while (keepUp);
    }

    private void cmdInterface(String input) throws IOException, InterruptedException {
        String cmd = input.trim().toLowerCase();

        // check cmd
        switch (cmd) {
            default:
                //System.out.println("Command not supported.");
                break;
            case "enter":
                cmdEnter();
                break;
            case "exit":
                cmdExit();
                break;
            case "table":
                cmdTable();
                break;
            case "info":
                enterInfo();
                break;
            case "quit":
                System.exit(0);
                break;
        }
    }

    private void cmdEnter( ) throws IOException, InterruptedException {
        // send "enter" to BNS
        if ( 1023<nsContn.addr.nsId ) {
            System.out.println("ID must [1-1023] !");
            return;
        }
        String msg = "enter " + nsContn.addr.nsId.toString() + " " + nsContn.addr.port;
        SendMsg msgBns = new SendMsg(nsContn.bns, false, msg);
        String response = msgBns.send();
        for (int i=0; i<25; i++) {
            TimeUnit.MILLISECONDS.sleep(200);
            if (null!=nsContn.tableHead){ break; }
        }
        enterInfo();
    }

    private void cmdExit( ) throws InterruptedException, IOException {
        // tell predc change succ to this.succ [ IP, port, nsID ]
        String newSuccMsg = "newSuccessor "  + nsContn.successor.IP + " " + nsContn.successor.port + " " + nsContn.successor.nsId.toString();
        SendMsg newSuccMsgSend = new SendMsg(nsContn.predcessor, false, newSuccMsg);
        String newSuccRsp = newSuccMsgSend.send();

        // tell succ change predc to this.predc  [ IP, port, nsID ]
        String newPredcMsg = "newpredcessor "  + nsContn.predcessor.IP + " " + nsContn.predcessor.port + " " + nsContn.predcessor.nsId.toString();
        SendMsg newPredcMsgSend = new SendMsg(nsContn.successor, false, newPredcMsg);
        String newPredcRsp = newPredcMsgSend.send();

        List<String> newNSTable = nsContn.table.slice(nsContn.tableHead, nsContn.addr.nsId);    // split table
        String addTableMsg = "addTable " + Arrays.toString(newNSTable.toArray()).replace(",", "").replace("[", "").replace("]", "");
        SendMsg sendTable = new SendMsg(nsContn.successor, false, addTableMsg);
        sendTable.send();

        for (int i=0; i<25; i++) {
            TimeUnit.MILLISECONDS.sleep(200);
            if (null!=nsContn.predcessor){ break; }
        }
        exitInfo();
    }

    public void enterInfo() {
        System.out.println(   "Successful entry:\n"
                        + "Keys: " + nsContn.tableHead + "-" + nsContn.tableTail + "\n"
                        + "Predecessor: " + nsContn.predcessor.nsId + "\n"
                        + "Successor: " + nsContn.successor.nsId + "\n"
                        + "Traverse: " + nsContn.trvHis
        );
    }


    public void exitInfo() {
        System.out.println(   "Successful exit:\n"
                        + "Keys: " + nsContn.tableHead + "-" + nsContn.tableTail + "\n"
                        + "Predecessor: " + nsContn.predcessor.nsId + "\n"
                        + "Successor: " + nsContn.successor.nsId + "\n"
        );
    }

    public void cmdTable( ) {
        nsContn.table.printTable();
    }

    public void trfStart (Boolean bg) {
        // bg traffic thread

    }

    public void operByKey(String oper, Integer key) throws IOException {

        /**
        DataInputStream recNportSkt = null;
        DataOutputStream sendNportSkt = null;
        for (NameServerContainer ns:allNsContn) {
            if ( key >= ns.tableHead && key <= ns.tableTail ) {
                try {
                    Socket nportSkt = new Socket( ns.IP, ns.port );
                    recNportSkt = new DataInputStream(nportSkt.getInputStream());
                    sendNportSkt = new DataOutputStream(nportSkt.getOutputStream());
                } catch (IOException eIO) {
                    System.out.println("Connect NS failed.");
                    //System.exit(0);
                }
                sendNportSkt.writeUTF(oper+ " " + key.toString());
                response = recNportSkt.readUTF();

        }
         */
    }


}
