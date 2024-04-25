package concurs.services;

import concurs.model.Participant;
import concurs.persistence.jdbc.DBRepositoryParticipant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceParticipant {
    private final DBRepositoryParticipant repoParticipant;

    public ServiceParticipant(DBRepositoryParticipant dbRepositoryParticipant){
        repoParticipant = dbRepositoryParticipant;
    }

    public Participant addParticipant(String nume, Integer varsta, List<Integer> probe){
        Participant participant = new Participant(nume, varsta, probe);
        return repoParticipant.save(participant);
    }

    public List<Participant> findAllParticipanti(){
        return StreamSupport.stream(repoParticipant.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public Participant findParticipant(Integer id){
        return repoParticipant.findOne(id);
    }

    public String convertListToString(List<Integer> probe){
        StringBuilder result = new StringBuilder();
        for (Integer proba : probe){
            result.append(proba.toString());
            result.append(" ");
        }
        return result.toString();
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

    public List<Participant> findParticipantiWithString(String searchString){
        List<Participant> resultName = StreamSupport.stream(
                repoParticipant.findAllByName(searchString).spliterator(), false).toList();

        if(isNumeric(searchString)){
            return StreamSupport.stream(
                    repoParticipant.findAllByAge(Integer.parseInt(searchString)).spliterator(), false).toList();
        }

        return resultName;
    }
}
