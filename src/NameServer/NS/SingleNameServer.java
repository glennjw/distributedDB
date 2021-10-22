package NameServer.NS;

import NameServer.NS.NameServerContainer;
import NameServer.NS.NameServerIntf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingleNameServer {
    NameServerContainer nsContn = null;

    public SingleNameServer(String fileName) {
        List<String> fileContents = new ArrayList<>();
        try {
            List<String> allLines = Files.readAllLines(Paths.get(fileName));
            for (String line : allLines) {
                fileContents.add( line );
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Cannot open file.");
            System.exit(1);
        }
        this.nsContn = new NameServerContainer(Integer.parseInt( fileContents.get(0).trim() ), "localhost", Integer.parseInt( fileContents.get(1).trim() ) );
        this.nsContn.table = new NameTable();
        this.nsContn.bns = new NameServerAddr( fileContents.get(2).split(" ")[0], Integer.parseInt(fileContents.get(2).split(" ")[1]), Integer.parseInt( fileContents.get(0).trim()) );

    }

    public void run() throws IOException {
        ServerSocket nportServerSkt = new ServerSocket(nsContn.addr.port);

        NameServerIntf nsHandler = new NameServerIntf( nportServerSkt, nsContn );
        Thread nsHandlerThr = new Thread( nsHandler );
        nsHandlerThr.start();

        // CLI
        NSExecutor bnsThr = new NSExecutor( nportServerSkt, nsContn );
        bnsThr.run();

    }

}
