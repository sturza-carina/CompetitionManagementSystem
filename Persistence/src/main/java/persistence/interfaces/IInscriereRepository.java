package persistence.interfaces;


import model.Inscriere;
import model.Participant;

import java.util.List;

public interface IInscriereRepository extends Repository<Inscriere, Long> {
    Iterable<Inscriere> findByParticipant(Long idParticipant);

    Iterable<Inscriere> findByProba(Long idProba);

    int countByProba(Long idProba);

    int countByParticipant(Long idParticipant);

    Inscriere findByParticipantAndProba(Long idParticipant, Long idProba);

    boolean exists(Long idParticipant, Long idProba);

    List<Participant> getParticipantByProbaSiCategorie(Long idProba, String categorieVarsta);
}
