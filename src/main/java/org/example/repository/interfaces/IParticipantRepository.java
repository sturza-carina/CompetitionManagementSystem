package org.example.repository.interfaces;

import org.example.domain.Participant;

public interface IParticipantRepository extends Repository<Participant, Long> {
    Participant findByCnp(String cnp);
}
