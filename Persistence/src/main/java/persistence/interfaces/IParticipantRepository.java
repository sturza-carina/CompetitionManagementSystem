package persistence.interfaces;


import model.Participant;

public interface IParticipantRepository extends Repository<Participant, Long> {
    Participant findByCnp(String cnp);
}
