package services;

import model.Inscriere;
import model.Operator;
import model.Participant;
import model.Proba;

import java.util.List;

public interface IServices {

    void login(Operator operator, IObserver client) throws InscriereException;

    void logout(Operator operator, IObserver client) throws InscriereException;

    List<Proba> getAllProbe() throws InscriereException;

    List<Inscriere> getAllInscrieri() throws InscriereException;

    List<Participant> getParticipantByProbaSiCategorie(long idProba, String categorieVarsta) throws InscriereException;

    void inscriereParticipant(String nume, String cnp, List<Long> probeIds) throws InscriereException;

    void stergeProbaParticipant(String cnp, long idProba) throws InscriereException;

    Participant getParticipantById(long idParticipant) throws InscriereException;

    Proba getProbaById(long idProba) throws InscriereException;
}
