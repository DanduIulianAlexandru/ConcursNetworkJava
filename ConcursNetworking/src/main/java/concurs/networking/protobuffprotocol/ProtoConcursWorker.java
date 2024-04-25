package concurs.networking.protobuffprotocol;

import concurs.model.*;
import concurs.networking.dto.AdminDTO;
import concurs.networking.dto.DTOUtils;
import concurs.networking.dto.ParticipantDTO;
import concurs.networking.dto.ProbaDTO;
import concurs.networking.rpcprotocol.Request;
import concurs.networking.rpcprotocol.RequestType;
import concurs.networking.rpcprotocol.Response;
import concurs.networking.rpcprotocol.ResponseType;
import concurs.services.ConcursException;
import concurs.services.IConcursObserver;
import concurs.services.IConcursServices;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ProtoConcursWorker implements Runnable, IConcursObserver {
    private final IConcursServices server;
    private final Socket connection;

    private InputStream input;
    private OutputStream output;
    private volatile boolean connected;

    public ProtoConcursWorker(IConcursServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = connection.getOutputStream();
            input = connection.getInputStream();
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (connected) {
            try {
                System.out.println("Waiting requests ...");
                ConcursProtobufs.ConcursRequest request = ConcursProtobufs.ConcursRequest.parseDelimitedFrom(input);
                System.out.println("Request received: " + request);
                ConcursProtobufs.ConcursResponse response = handleRequest(request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    @Override
    public void participantAdded(Participant participant) throws ConcursException {
        try {
            sendResponse(ProtoUtils.createAddedParticipantResponse(participant));
            System.out.println("Participant added: " + participant);
        } catch (IOException e) {
            throw new ConcursException("Sending error: " + e);
        }
    }

    private ConcursProtobufs.ConcursResponse handleRequest(ConcursProtobufs.ConcursRequest request) {
        ConcursProtobufs.ConcursResponse response = null;
        if (request.getType() == ConcursProtobufs.ConcursRequest.Type.Login) {
            System.out.println("Login request ..." + request.getType());
            Admin admin = ProtoUtils.getAdmin(request);
            try {
                Admin admin1 = server.login(admin, this);
                return ProtoUtils.createOkForLoginResponse(admin1);
            } catch (ConcursException e) {
                connected = false;
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        if (request.getType() == ConcursProtobufs.ConcursRequest.Type.Logout){
            System.out.println("Logout request...");
            Admin admin = ProtoUtils.getAdmin(request);
            try{
                server.logout(admin, this);
                connected = false;
                return ProtoUtils.createOkResponse();
            } catch (ConcursException e) {
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }

        if (request.getType() == ConcursProtobufs.ConcursRequest.Type.GetProbe){
            System.out.println("Probe requested");
            try{
                List<Proba> probe = server.getProbe();
                return ProtoUtils.createGetProbeResponse(probe);
            } catch (ConcursException e) {
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }

        if (request.getType() == ConcursProtobufs.ConcursRequest.Type.GetParticipanti){
            System.out.println("Participanti requested");
            try{
                List<Participant> participanti = server.getParticipanti();
                return ProtoUtils.createGetParticipantiResponse(participanti);
            } catch (ConcursException e) {
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }

        if (request.getType() == ConcursProtobufs.ConcursRequest.Type.GetProba){
            System.out.println("Proba requested");
            Integer id = ProtoUtils.getIdProba(request);
            try{
                Proba result = server.getProba(id);
                if(result == null){
                    result = new Proba(Distanta.M50, Stil.MIXT);
                    result.setId(-1);
                    result.setNrParticipanti(-1);
                }
                return ProtoUtils.createGetProbaResponse(result);
            } catch (ConcursException e) {
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }

        if(request.getType() == ConcursProtobufs.ConcursRequest.Type.AddParticipant){
            System.out.println("Add participant request");
            Participant participant = ProtoUtils.getParticipant(request);
            try{
                server.addParticipant(participant);
                return ProtoUtils.createOkResponse();
            } catch (ConcursException e){
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }

        if (request.getType() == ConcursProtobufs.ConcursRequest.Type.CautaParticipanti){
            System.out.println("Cauta participanti requested");
            try{
                List<Participant> participanti = server.getParticipantiByString(ProtoUtils.getSearchString(request));
                return ProtoUtils.createCautaParticipantiResponse(participanti);
            } catch (ConcursException e) {
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }

        return response;
    }

    private void sendResponse(ConcursProtobufs.ConcursResponse response) throws IOException {
        System.out.println("sending response " + response);
        response.writeDelimitedTo(output);
        //output.writeObject(response);
        output.flush();
    }
}
