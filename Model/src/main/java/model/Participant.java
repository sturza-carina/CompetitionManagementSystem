package model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@jakarta.persistence.Entity
@Table(name = "participanti")
@AttributeOverride(name = "id", column = @Column(name = "id_participant"))
public class Participant extends Entity<Long> {
    @Column(name = "nume", nullable = false)
    private String nume;
    @Column(name = "cnp", nullable = false, unique = true)
    private String cnp;
    @Column(name = "varsta", nullable = false)
    private int varsta;

    // constructor implicit
    public Participant() {}

    public Participant(String nume, String cnp, int varsta) {
        this.nume = nume;
        this.cnp = cnp;
        this.varsta = varsta;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public int getVarsta() {
        return varsta;
    }

    public void setVarsta(int varsta) {
        this.varsta = varsta;
    }

}
