package concurs.networking.dto;

import java.io.Serializable;

public class ProbaDTO implements Serializable {
    private String id;
    private String distanta;
    private String stil;
    private String nrParticipanti;

    public ProbaDTO() {
    }

    public ProbaDTO(String id, String distanta, String stil, String nrParticipanti) {
        this.id = id;
        this.distanta = distanta;
        this.stil = stil;
        this.nrParticipanti = nrParticipanti;
    }

    public String getId() {
        return id;
    }

    public String getDistanta() {
        return distanta;
    }

    public String getStil() {
        return stil;
    }

    public String getNrParticipanti() {
        return nrParticipanti;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDistanta(String distanta) {
        this.distanta = distanta;
    }

    public void setStil(String stil) {
        this.stil = stil;
    }

    public void setNrParticipanti(String nrParticipanti) {
        this.nrParticipanti = nrParticipanti;
    }

    @Override
    public String toString() {
        return "ProbaDTO{" +
                "id='" + id + '\'' +
                ", distanta='" + distanta + '\'' +
                ", stil='" + stil + '\'' +
                ", nrParticipanti='" + nrParticipanti + '\'' +
                '}';
    }
}
