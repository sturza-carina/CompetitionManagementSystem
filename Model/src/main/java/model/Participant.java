package model;


public class Participant extends Entity<Long> {
    private String nume;
    private String cnp;
    private int varsta;

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
