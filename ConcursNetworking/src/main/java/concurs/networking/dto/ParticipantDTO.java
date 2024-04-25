package concurs.networking.dto;

import java.io.Serializable;

public class ParticipantDTO implements Serializable {
    private String id;
    private String nume;
    private String varsta;
    private String probe;

    public ParticipantDTO() {
    }

    public ParticipantDTO(String id, String nume, String varsta, String probe) {
        this.id = id;
        this.nume = nume;
        this.varsta = varsta;
        this.probe = probe;
    }

    public String getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public String getVarsta() {
        return varsta;
    }

    public String getProbe() {
        return probe;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setVarsta(String varsta) {
        this.varsta = varsta;
    }

    public void setProbe(String probe) {
        this.probe = probe;
    }

    @Override
    public String toString() {
        return "ParticipantDTO{" +
                "id='" + id + '\'' +
                ", nume='" + nume + '\'' +
                ", varsta='" + varsta + '\'' +
                ", probe='" + probe + '\'' +
                '}';
    }
}
