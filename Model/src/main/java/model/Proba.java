package model;

import jakarta.persistence.*;

@jakarta.persistence.Entity
@Table(name = "probe")
@AttributeOverride(name = "id", column = @Column(name = "id_proba"))
public class Proba extends Entity<Long> {
    @Column(name = "nume", nullable = false)
    private String nume;
    @Column(name = "categorie_varsta", nullable = false)
    private String categorieVarsta;

    // constructor implicit
    public Proba() {}

    public Proba(String nume, String categorie) {
        this.nume = nume;
        this.categorieVarsta = categorie;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getCategorie() {
        return categorieVarsta;
    }

    public void setCategorie(String categorie) {
        this.categorieVarsta = categorie;
    }

}
