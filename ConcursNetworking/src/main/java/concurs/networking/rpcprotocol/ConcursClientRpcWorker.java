package concurs.networking.rpcprotocol;

import concurs.model.Admin;
import concurs.model.Participant;
import concurs.model.Proba;
import concurs.networking.dto.AdminDTO;
import concurs.networking.dto.DTOUtils;
import concurs.networking.dto.ParticipantDTO;
import concurs.networking.dto.ProbaDTO;
import concurs.services.ConcursException;
import concurs.services.IConcursObserver;
import concurs.services.IConcursServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ConcursClientRpcWorker implements Runnable, IConcursObserver {
    private final IConcursServices server;
    private final Socket connection;

    private static final Response okResponse = new Response.Builder().type(ResponseType.OK).build();
    private static final Response errorResponse = new Response.Builder().type(ResponseType.ERROR).build();

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ConcursClientRpcWorker(IConcursServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
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
        ParticipantDTO participantDTO = DTOUtils.getDTO(participant);
        Response response = new Response.Builder().type(ResponseType.ADDED_PARTICIPANT).data(participantDTO).build();
        try {
            sendResponse(response);
            System.out.println("Participant added: " + participant);
        } catch (IOException e) {
            throw new ConcursException("Sending error: " + e);
        }
    }

    private Response handleRequest(Request request) {
        Response response = null;
        if (request.type() == RequestType.LOGIN) {
            System.out.println("Login request ..." + request.type());
            AdminDTO adminDTO = (AdminDTO) request.data();
            Admin admin = DTOUtils.getFromDTO(adminDTO);
            try {
                Admin admin1 = server.login(admin, this);
                return new Response.Builder().data(DTOUtils.getDTO(admin1)).type(ResponseType.OK).build();
            } catch (ConcursException e) {
                connected = false;
                return new Response.Builder().data(e.getMessage()).type(ResponseType.ERROR).build();
            }
        }
        if (request.type() == RequestType.LOGOUT){
            System.out.println("Logout request");
            AdminDTO adminDTO = (AdminDTO) request.data();
            Admin admin = DTOUtils.getFromDTO(adminDTO);
            try{
                server.logout(admin, this);
                connected = false;
                return okResponse;
            } catch (ConcursException e) {
                return new Response.Builder().data(e.getMessage()).type(ResponseType.ERROR).build();
            }
        }

        if (request.type() == RequestType.GET_PROBE){
            System.out.println("Probe requested");
            try{
                List<ProbaDTO> probeDTO = DTOUtils.getProbeDTO(server.getProbe());
                return new Response.Builder().type(ResponseType.OK).data(probeDTO).build();
            } catch (ConcursException e) {
                return new Response.Builder().data(e.getMessage()).type(ResponseType.ERROR).build();
            }
        }

        if (request.type() == RequestType.GET_PARTICIPANTI){
            System.out.println("Participanti requested");
            try{
                List<ParticipantDTO> participantDTO = DTOUtils.getParticipantiDTO(server.getParticipanti());
                return new Response.Builder().type(ResponseType.OK).data(participantDTO).build();
            } catch (ConcursException e) {
                return new Response.Builder().data(e.getMessage()).type(ResponseType.ERROR).build();
            }
        }

        if (request.type() == RequestType.GET_PROBA){
            System.out.println("Proba requested");
            String idS = (String) request.data();
            Integer id = Integer.parseInt(idS);
            try{
                Proba result = server.getProba(id);
                if(result == null){
                    return new Response.Builder().type(ResponseType.OK).data(null).build();
                }
                ProbaDTO probaDTO = DTOUtils.getDTO(result);
                return new Response.Builder().type(ResponseType.OK).data(probaDTO).build();
            } catch (ConcursException e) {
                return new Response.Builder().data(e.getMessage()).type(ResponseType.ERROR).build();
            }
        }

        if(request.type() == RequestType.ADD_PARTICIPANT){
            System.out.println("Add participant request");
            ParticipantDTO participantDTO = (ParticipantDTO) request.data();
            Participant participant = DTOUtils.getFromDTO(participantDTO);
            try{
                server.addParticipant(participant);
                return okResponse;
            } catch (ConcursException e){
                return new Response.Builder().data(e.getMessage()).type(ResponseType.ERROR).build();
            }
        }

        if (request.type() == RequestType.CAUTA_PARTICIPANTI){
            System.out.println("Cauta participanti requested");
            try{
                List<ParticipantDTO> participantDTO = DTOUtils.getParticipantiDTO(
                        server.getParticipantiByString((String) request.data()));
                return new Response.Builder().type(ResponseType.OK).data(participantDTO).build();
            } catch (ConcursException e) {
                return new Response.Builder().data(e.getMessage()).type(ResponseType.ERROR).build();
            }
        }

        return response;
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("sending response " + response);
        output.writeObject(response);
        output.flush();
    }
}
