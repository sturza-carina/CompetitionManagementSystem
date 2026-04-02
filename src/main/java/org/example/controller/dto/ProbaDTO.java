package org.example.controller.dto;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ProbaDTO {
    private final long id;
    private final String nume;
    private final String categorieVarsta;
    private final long nrInscrisi;
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    public ProbaDTO(long id, String nume, String categorieVarsta, long nrInscrisi) {
        this.id = id;
        this.nume = nume;
        this.categorieVarsta = categorieVarsta;
        this.nrInscrisi = nrInscrisi;
    }

    public long getId() { return id; }
    public String getNume() { return nume; }
    public String getCategorieVarsta() { return categorieVarsta; }
    public long getNrInscrisi() { return nrInscrisi; }

    public BooleanProperty selectedProperty() { return selected; }
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean value) { selected.set(value); }
}
