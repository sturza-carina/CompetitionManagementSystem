package network.dto;

import java.io.Serializable;

public class ProbaDTO implements Serializable {
    private Long id_proba;
    private String nume;
    private String categorieVarsta;

    public ProbaDTO(Long id_proba, String nume, String categorieVarsta) {
        this.id_proba = id_proba;
        this.nume = nume;
        this.categorieVarsta = categorieVarsta;
    }

    public Long getId() {
        return id_proba;
    }

    public String getNume() {
        return nume;
    }

    public String getCategorieVarsta() {
        return categorieVarsta;
    }

    @Override
    public String toString() {
        return "ProbaDTO[" + id_proba + " " + nume +  " "  + categorieVarsta + "]";
    }
}
