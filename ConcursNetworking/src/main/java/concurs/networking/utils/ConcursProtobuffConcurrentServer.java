package concurs.networking.utils;

import concurs.networking.protobuffprotocol.ProtoConcursWorker;
import concurs.services.IConcursServices;

import java.net.Socket;

public class ConcursProtobuffConcurrentServer extends AbsConcurrentServer {
    private IConcursServices server;

    public ConcursProtobuffConcurrentServer(int port, IConcursServices server) {
        super(port);
        this.server = server;
        System.out.println("Concurs - ConcursProtobuffConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ProtoConcursWorker worker = new ProtoConcursWorker(server, client);
        Thread tw = new Thread(worker);
        return tw;
    }
}
