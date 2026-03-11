package org.example.domain;

public class Proba extends Entity<Long> {
    private String nume;
    private String categorieVarsta;

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
