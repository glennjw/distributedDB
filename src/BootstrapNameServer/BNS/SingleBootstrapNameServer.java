package BootstrapNameServer.BNS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SingleBootstrapNameServer {

    //List defaultHashingRange = Arrays.asList(0, 1023);
    Integer tableHead = 1;
    Integer tableTail = 1023;
    NameServerContainer nsContn;

    public SingleBootstrapNameServer(String fileName) {
        List<String> fileContents = new ArrayList<>();
        try {
            List<String> allLines = Files.readAllLines(Paths.get(fileName));
            for (String line : allLines) {
                for (String each:line.split(" "))
                    fileContents.add( each );
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("Cannot open file.");
        }
        NameServerAddr addr = new NameServerAddr("localhost", Integer.parseInt( fileContents.get(1).trim() ), Integer.parseInt( fileContents.get(0).trim() ));
        this.nsContn = new NameServerContainer( addr, tableHead, tableTail );   // leave 0 to BNS
        nsContn.createTable( new ArrayList<String>(fileContents.subList(2, fileContents.size())) );
        nsContn.predcessor = addr;
        //nsContn.successor = null;
    }

    public void run() throws IOException {

        ServerSocket nportServerSkt = new ServerSocket(nsContn.addr.port);

        // run NS handler thread
        NameServerIntf nsHandler = new NameServerIntf( nportServerSkt, nsContn );
        Thread nsHandlerThr = new Thread( nsHandler );
        nsHandlerThr.start();

        // run BNS
        BNSExecutor bnsThr = new BNSExecutor( nportServerSkt, nsContn );
        bnsThr.run();

    }

}
