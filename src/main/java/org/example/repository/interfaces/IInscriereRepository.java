package org.example.repository.interfaces;

import org.example.domain.Inscriere;

public interface IInscriereRepository extends Repository<Inscriere, Long> {
    Iterable<Inscriere> findByParticipant(Long idParticipant);

    Iterable<Inscriere> findByProba(Long idProba);

    int countByProba(Long idProba);
}
