package client.gui.dto;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ProbaDTO {
    private final long id;
    private final String nume;
    private final String categorieVarsta;
    private final IntegerProperty nrInscrisi = new SimpleIntegerProperty();
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    public ProbaDTO(long id, String nume, String categorieVarsta, long nrInscrisi) {
        this.id = id;
        this.nume = nume;
        this.categorieVarsta = categorieVarsta;
        setNrInscrisi(nrInscrisi);
    }

    public long getId() { return id; }
    public String getNume() { return nume; }
    public String getCategorieVarsta() { return categorieVarsta; }
    public int getNrInscrisi() { return nrInscrisi.get(); }

    public BooleanProperty selectedProperty() { return selected; }
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean value) { selected.set(value); }

    public void setNrInscrisi(long val) {
        nrInscrisi.set((int) val);
    }

    public IntegerProperty nrInscrisiProperty() { return nrInscrisi; }
}
