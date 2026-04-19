package network.dto;

import java.io.Serializable;

public class ParticipantDTO implements Serializable {
    private Long id_participant;
    private String nume;
    private String cnp;
    private int varsta;

    public ParticipantDTO(Long id_participant, String nume, String cnp, int varsta) {
        this.id_participant = id_participant;
        this.nume = nume;
        this.cnp = cnp;
        this.varsta = varsta;
    }

    public Long getId() {
        return id_participant;
    }

    public String getNume() {
        return nume;
    }

    public String getCnp() {
        return cnp;
    }

    public int getVarsta() {
        return varsta;
    }

    @Override
    public String toString() {
        return "ParticipantDTO[" + id_participant + " " + nume + " " + cnp + " " + varsta + "]";

    }
}
