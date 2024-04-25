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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ConcursServicesRpcProxy implements IConcursServices {
    private String host;
    private int port;
    private IConcursObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private BlockingDeque<Response> qresponses;
    private volatile boolean finished;

    public ConcursServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<>();
    }

    public void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Admin login(Admin admin, IConcursObserver client) throws ConcursException {
        initializeConnection();
        AdminDTO adminDTO = DTOUtils.getDTO(admin);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(adminDTO).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = client;
            AdminDTO adminDTO1 = (AdminDTO) response.data();
            return DTOUtils.getFromDTO(adminDTO1);
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            throw new ConcursException(err);
        }
        return null;
    }

    public void logout(Admin admin, IConcursObserver client) throws ConcursException {
        AdminDTO adminDTO = DTOUtils.getDTO(admin);
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(adminDTO).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ConcursException(err);
        }
    }

    @Override
    public void addParticipant(Participant participant) throws ConcursException {
        ParticipantDTO participantDTO = DTOUtils.getDTO(participant);
        Request request = new Request.Builder().type(RequestType.ADD_PARTICIPANT).data(participantDTO).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ConcursException(err);
        }
    }

    @Override
    public List<Proba> getProbe() throws ConcursException {
        Request request = new Request.Builder().type(RequestType.GET_PROBE).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ConcursException(err);
        }
        List<ProbaDTO> probeDTO = (List<ProbaDTO>) response.data();
        return DTOUtils.getProbeFromDTO(probeDTO);
    }

    @Override
    public List<Participant> getParticipanti() throws ConcursException {
        Request request = new Request.Builder().type(RequestType.GET_PARTICIPANTI).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ConcursException(err);
        }
        List<ParticipantDTO> participantiDTO = (List<ParticipantDTO>) response.data();
        return DTOUtils.getParticipantiFromDTO(participantiDTO);
    }

    @Override
    public Proba getProba(Integer id) throws ConcursException {
        Request request = new Request.Builder().type(RequestType.GET_PROBA).data(id.toString()).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ConcursException(err);
        }
        if (response.data() == null) {
            return null;
        }
        ProbaDTO probaDTO = (ProbaDTO) response.data();
        return DTOUtils.getFromDTO(probaDTO);
    }

    @Override
    public List<Participant> getParticipantiByString(String searchString) throws ConcursException {
        Request request = new Request.Builder().type(RequestType.CAUTA_PARTICIPANTI).data(searchString).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ConcursException(err);
        }
        if (response.data() == null) {
            return new ArrayList<>();
        }
        List<ParticipantDTO> participantiDTO = (List<ParticipantDTO>) response.data();
        return DTOUtils.getParticipantiFromDTO(participantiDTO);
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request) throws ConcursException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new ConcursException("Error sending object " + e);
        }

    }

    private Response readResponse() throws ConcursException {
        Response response = null;
        try {

            response = qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.ADDED_PARTICIPANT) {
            Participant participant = DTOUtils.getFromDTO((ParticipantDTO) response.data());
            System.out.println("Participant added " + participant);
            try {
                client.participantAdded(participant);
            } catch (ConcursException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.ADDED_PARTICIPANT;
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("response received " + response);
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {

                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
