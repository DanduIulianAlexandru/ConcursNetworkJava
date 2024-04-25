package concurs.services;

import concurs.model.Participant;

public interface IConcursObserver {
    void participantAdded(Participant participant) throws ConcursException;
}
