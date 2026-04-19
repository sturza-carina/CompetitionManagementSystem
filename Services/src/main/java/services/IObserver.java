package services;

import model.Inscriere;

public interface IObserver {
    void inscriereAdded(Inscriere inscriere) throws InscriereException;

    void inscriereDeleted(Inscriere inscriere) throws InscriereException;
}
