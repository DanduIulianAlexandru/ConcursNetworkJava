package concurs.services;

import concurs.model.Admin;
import concurs.model.Participant;
import concurs.model.Proba;

import java.util.List;

public interface IConcursServices {
    Admin login(Admin admin, IConcursObserver client) throws ConcursException;
    void logout(Admin admin, IConcursObserver client) throws ConcursException;
    void addParticipant(Participant participant) throws ConcursException;
    List<Proba> getProbe() throws ConcursException;
    List<Participant> getParticipanti() throws ConcursException;
    Proba getProba(Integer id) throws ConcursException;
    List<Participant> getParticipantiByString(String searchString) throws ConcursException;
}
