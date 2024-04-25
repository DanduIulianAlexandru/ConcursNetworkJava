package concurs.services;

import concurs.model.Proba;
import concurs.persistence.jdbc.DBRepositoryProba;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceProba {
    private final DBRepositoryProba repoProba;

    public ServiceProba(DBRepositoryProba dbRepositoryProba){
        repoProba = dbRepositoryProba;
    }

    public List<Proba> findAllProbe(){
        return StreamSupport.stream(repoProba.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public Proba findProba(Integer id){
        return repoProba.findOne(id);
    }
}
