package org.example.repository;

import org.example.domain.Operator;
import org.example.domain.Participant;

public interface IParticipantRepository extends Repository<Participant, Long>{
    Participant findByCnp(String cnp);
}
