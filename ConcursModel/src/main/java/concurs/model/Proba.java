package concurs.model;
import java.util.Objects;

public class Proba extends Entity<Integer>{
    private Distanta distanta;
    private Stil stil;
    private Integer nrParticipanti;

    public Proba(Distanta distanta, Stil stil, Integer nrParticipanti){
        this.distanta = distanta;
        this.stil = stil;
        this.nrParticipanti = nrParticipanti;
    }

    public Proba(){
        this.distanta = Distanta.M50;
        this.stil = Stil.MIXT;
        this.nrParticipanti = -1;
    }

    public Proba(Distanta distanta, Stil stil) {
        this.distanta = distanta;
        this.stil = stil;
    }

    public Distanta getDistanta() {
        return distanta;
    }

    public void setDistanta(Distanta distanta) {
        this.distanta = distanta;
    }

    public Stil getStil() {
        return stil;
    }

    public void setStil(Stil stil) {
        this.stil = stil;
    }

    public Integer getNrParticipanti() {
        return nrParticipanti;
    }

    public void incrementNrParticipanti(){
        nrParticipanti++;
    }

    public void setNrParticipanti(Integer nrParticipanti) {
        this.nrParticipanti = nrParticipanti;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proba proba = (Proba) o;
        return getDistanta() == proba.getDistanta() && getStil() == proba.getStil();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDistanta(), getStil(), getNrParticipanti());
    }

    @Override
    public String toString() {
        return "Proba{" +
                "distanta=" + distanta +
                ", stil=" + stil +
                ", nrParticipanti=" + nrParticipanti +
                '}';
    }
}
