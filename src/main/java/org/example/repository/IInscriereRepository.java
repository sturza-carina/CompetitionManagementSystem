package org.example.repository;

import org.example.domain.Inscriere;
import org.example.domain.Operator;

public interface IInscriereRepository extends Repository<Inscriere, Long>{
    Iterable<Inscriere> findByParticipant(Long idParticipant);

    Iterable<Inscriere> findByProba(Long idProba);

    int countByProba(Long idProba);
}
