package concurs.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Participant extends Entity<Integer>{
    private String nume;
    private Integer varsta;
    private List<Integer> probe;

    public Participant(String nume, Integer varsta) {
        this.nume = nume;
        this.varsta = varsta;
        probe = new ArrayList<>();
    }

    public Participant(String nume, Integer varsta, List<Integer> probe){
        this.nume = nume;
        this.varsta = varsta;
        this.probe = probe;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Integer getVarsta() {
        return varsta;
    }

    public void setVarsta(Integer varsta) {
        this.varsta = varsta;
    }

    public List<Integer> getProbe() {
        return probe;
    }

    public void setProbe(List<Integer> probe) {
        this.probe = probe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(getNume(), that.getNume()) && Objects.equals(getVarsta(), that.getVarsta()) &&
                Objects.equals(getProbe(), that.getProbe()) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNume(), getVarsta(), getProbe());
    }

    @Override
    public String toString() {
        return "Participant{" +
                "nume='" + nume + '\'' +
                ", varsta=" + varsta +
                ", probe=" + probe +
                '}';
    }
}
