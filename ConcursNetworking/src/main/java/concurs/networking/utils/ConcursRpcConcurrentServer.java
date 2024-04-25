package concurs.networking.utils;

import concurs.networking.rpcprotocol.ConcursClientRpcWorker;
import concurs.services.IConcursServices;

import java.net.Socket;

public class ConcursRpcConcurrentServer extends AbsConcurrentServer {
    private final IConcursServices concursServer;

    public ConcursRpcConcurrentServer(int port, IConcursServices concursServer) {
        super(port);
        this.concursServer = concursServer;
        System.out.println("Concurs- ConcursRpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ConcursClientRpcWorker worker = new ConcursClientRpcWorker(concursServer, client);

        return new Thread(worker);
    }

    @Override
    public void stop() {
        System.out.println("Stopping services ...");
    }
}
