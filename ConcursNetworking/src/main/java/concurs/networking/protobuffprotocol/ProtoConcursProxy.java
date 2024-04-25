package concurs.networking.protobuffprotocol;

import concurs.model.Admin;
import concurs.model.Participant;
import concurs.model.Proba;
import concurs.services.ConcursException;
import concurs.services.IConcursObserver;
import concurs.services.IConcursServices;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ProtoConcursProxy implements IConcursServices {
    private String host;
    private int port;
    private IConcursObserver client;
    private InputStream input;
    private OutputStream output;
    private Socket connection;
    private BlockingDeque<ConcursProtobufs.ConcursResponse> qresponses;
    private volatile boolean finished;

    public ProtoConcursProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<>();
    }

    public void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = connection.getOutputStream();
            //output.flush();
            input = connection.getInputStream();        //new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Admin login(Admin admin, IConcursObserver client) throws ConcursException {
        initializeConnection();
        sendRequest(ProtoUtils.createLoginRequest(admin));
        ConcursProtobufs.ConcursResponse response = readResponse();
        if (response.getType() == ConcursProtobufs.ConcursResponse.Type.Ok) {
            this.client = client;
            return ProtoUtils.getAdmin(response);
        }
        if (response.getType() == ConcursProtobufs.ConcursResponse.Type.Error) {
            String err = ProtoUtils.getError(response);
            closeConnection();
            throw new ConcursException(err);
        }
        return null;
    }

    public void logout(Admin admin, IConcursObserver client) throws ConcursException {
        sendRequest(ProtoUtils.createLogoutRequest(admin));
        ConcursProtobufs.ConcursResponse response = readResponse();
        closeConnection();
        if (response.getType() == ConcursProtobufs.ConcursResponse.Type.Error) {
            String err = ProtoUtils.getError(response);
            throw new ConcursException(err);
        }
    }

    @Override
    public void addParticipant(Participant participant) throws ConcursException {
        sendRequest(ProtoUtils.createAddParticipant(participant));
        ConcursProtobufs.ConcursResponse response = readResponse();
        if (response.getType() == ConcursProtobufs.ConcursResponse.Type.Error) {
            String err = ProtoUtils.getError(response);
            throw new ConcursException(err);
        }
    }

    @Override
    public List<Proba> getProbe() throws ConcursException {
        sendRequest(ProtoUtils.createGetProbeRequest());
        ConcursProtobufs.ConcursResponse response = readResponse();
        if (response.getType() == ConcursProtobufs.ConcursResponse.Type.Error) {
            String err = ProtoUtils.getError(response);
            throw new ConcursException(err);
        }
        return ProtoUtils.getProbe(response);
    }

    @Override
    public List<Participant> getParticipanti() throws ConcursException {
        sendRequest(ProtoUtils.createGetParticipanti());
        ConcursProtobufs.ConcursResponse response = readResponse();
        if (response.getType() == ConcursProtobufs.ConcursResponse.Type.Error) {
            String err = ProtoUtils.getError(response);
            throw new ConcursException(err);
        }
        return ProtoUtils.getParticipanti(response);
    }

    @Override
    public Proba getProba(Integer id) throws ConcursException {
        sendRequest(ProtoUtils.createGetProba(id));
        ConcursProtobufs.ConcursResponse response = readResponse();
        if (response.getType() == ConcursProtobufs.ConcursResponse.Type.Error) {
            String err = ProtoUtils.getError(response);
            throw new ConcursException(err);
        }
        if (ProtoUtils.getProba(response).getId() == -1) {
            return null;
        }
        return ProtoUtils.getProba(response);
    }

    @Override
    public List<Participant> getParticipantiByString(String searchString) throws ConcursException {
        sendRequest(ProtoUtils.createCautaParticipantiRequest(searchString));
        ConcursProtobufs.ConcursResponse response = readResponse();
        if (response.getType() == ConcursProtobufs.ConcursResponse.Type.Error) {
            String err = ProtoUtils.getError(response);
            throw new ConcursException(err);
        }
        return ProtoUtils.getParticipanti(response);
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

    private void sendRequest(ConcursProtobufs.ConcursRequest request) throws ConcursException {
        try {
            System.out.println("Sending request ..." + request);
            //output.writeObject(request);
            request.writeDelimitedTo(output);
            output.flush();
        } catch (IOException e) {
            throw new ConcursException("Error sending object " + e);
        }

    }

    private ConcursProtobufs.ConcursResponse readResponse() throws ConcursException {
        ConcursProtobufs.ConcursResponse response = null;
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

    private void handleUpdate(ConcursProtobufs.ConcursResponse response) {
        if (response.getType() == ConcursProtobufs.ConcursResponse.Type.AddedParticipant) {
            Participant participant = ProtoUtils.getParticipant(response);
            System.out.println("Participant added " + participant);
            try {
                client.participantAdded(participant);
            } catch (ConcursException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdate(ConcursProtobufs.ConcursResponse response) {
        return response.getType() == ConcursProtobufs.ConcursResponse.Type.AddedParticipant;
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    ConcursProtobufs.ConcursResponse response = ConcursProtobufs.ConcursResponse.parseDelimitedFrom(input);
                    System.out.println("response received " + response);
                    if (isUpdate(response)) {
                        handleUpdate(response);
                    } else {

                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
