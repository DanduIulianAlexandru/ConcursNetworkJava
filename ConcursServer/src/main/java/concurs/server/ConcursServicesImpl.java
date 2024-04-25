package concurs.server;

import concurs.model.Admin;
import concurs.model.Participant;
import concurs.model.Proba;
import concurs.model.validators.ValidationException;
import concurs.networking.dto.DTOUtils;
import concurs.persistence.jdbc.DBRepositoryHbm;
import concurs.persistence.jdbc.DBRepositoryParticipant;
import concurs.persistence.jdbc.DBRepositoryProba;
import concurs.services.ConcursException;
import concurs.services.IConcursObserver;
import concurs.services.IConcursServices;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConcursServicesImpl implements IConcursServices {
    private DBRepositoryHbm repositoryAdmin;
    private DBRepositoryParticipant repositoryParticipant;
    private DBRepositoryProba repositoryProba;

    private Map<Integer, IConcursObserver> loggedAdmins;
    private final int defaultThreadsNo = 5;


    public ConcursServicesImpl(DBRepositoryHbm repositoryAdmin, DBRepositoryParticipant repositoryParticipant,
                               DBRepositoryProba repositoryProba) {
        this.repositoryAdmin = repositoryAdmin;
        this.repositoryParticipant = repositoryParticipant;
        this.repositoryProba = repositoryProba;
        this.loggedAdmins = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Admin login(Admin admin, IConcursObserver client) throws ConcursException {
        Admin adminL = repositoryAdmin.findBy(admin.getUsername(), admin.getPassword());
        if (adminL != null) {
            if (loggedAdmins.get(adminL.getId()) != null) {
                throw new ConcursException("Admin already logged in.");
            }
            loggedAdmins.put(adminL.getId(), client);
            return adminL;
        } else {
            throw new ConcursException("Authentication failed.");
        }
    }

    @Override
    public synchronized void logout(Admin admin, IConcursObserver client) throws ConcursException {
        IConcursObserver localAdmin = loggedAdmins.remove(admin.getId());
        if (localAdmin == null){
            throw new ConcursException(localAdmin + " is not logged in.");
        }
    }

    @Override
    public synchronized void addParticipant(Participant participant) throws ConcursException {
        if(participant == null){
            throw new ConcursException("Participant is null");
        }
        try {
            repositoryParticipant.save(participant);
            List<Integer> probeID = participant.getProbe();
            for(Integer id : probeID){
                repositoryProba.incrementNr(id);
            }
            notifyParticipantAdded(participant);
        }catch (ValidationException e){
            throw new ConcursException(e.getMessage());
        }
    }

    @Override
    public synchronized List<Proba> getProbe() {
        return StreamSupport.stream(repositoryProba.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public synchronized List<Participant> getParticipanti() {
        return StreamSupport.stream(repositoryParticipant.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public synchronized Proba getProba(Integer id) throws ConcursException, ValidationException {
        if(id == null){
            throw new ConcursException("Id is null");
        }
        return repositoryProba.findOne(id);
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public List<Participant> getParticipantiByString(String searchString) throws ConcursException {
        if(searchString == null){
            throw new ConcursException("Search string is null");
        }
        List<Participant> resultName = StreamSupport.stream(
                repositoryParticipant.findAllByName(searchString).spliterator(), false).toList();

        if(isNumeric(searchString)){
            return StreamSupport.stream(
                    repositoryParticipant.findAllByAge(Integer.parseInt(searchString)).spliterator(), false).toList();
        }

        return resultName;
    }

    private void notifyParticipantAdded(Participant participant) throws ConcursException {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);

        for(Map.Entry<Integer, IConcursObserver> client : loggedAdmins.entrySet()){
            if(client != null){
                executor.execute(() ->{
                    try{
                        System.out.println("Notifying admin " + client.getKey() + " participant was added");
                        client.getValue().participantAdded(participant);
                    } catch (ConcursException e){
                        System.out.println("Error notifying admin " + client.getKey());
                    }
                });
            }
        }
    }
}
