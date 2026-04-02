package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.Inscriere;
import org.example.domain.Operator;
import org.example.domain.Participant;
import org.example.domain.Proba;
import org.example.exceptions.ServiceException;
import org.example.repository.interfaces.IInscriereRepository;
import org.example.repository.interfaces.IOperatorRepository;
import org.example.repository.interfaces.IParticipantRepository;
import org.example.repository.interfaces.IProbaRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.List;



public class Service {
    private static final Logger logger = LogManager.getLogger(Service.class);

    private IParticipantRepository participantDBRepo;
    private IInscriereRepository inscriereDBRepo;
    private IOperatorRepository operatorDBRepo;
    private IProbaRepository  probaDBRepo;

    public Service(IInscriereRepository inscriereDBRepo,  IParticipantRepository participantDBRepo,
                   IProbaRepository probaDBRepo, IOperatorRepository operatorDBRepo) {
        this.inscriereDBRepo = inscriereDBRepo;
        this.participantDBRepo = participantDBRepo;
        this.operatorDBRepo = operatorDBRepo;
        this.probaDBRepo = probaDBRepo;

    }

    public Operator login(String username, String password) {
        logger.info("login attempt for: " + username);

        Operator operator = operatorDBRepo.findByUsername(username);

        if(operator == null)
            throw new ServiceException("username inexistent!");

        if(! BCrypt.checkpw(password, operator.getPassword()))
            throw new ServiceException("parola incorecta!");

        return operator;
    }

    public List<Proba> getAllProbe() {
        return (List<Proba>) probaDBRepo.findAll();
    }

    public List<Inscriere> getAllInscrieri() {
        return (List<Inscriere>) inscriereDBRepo.findAll();
    }


    public List<Participant> getParticipantByProbaSiCategorie(long idProba, String categorieVarsta) {
        if ( categorieVarsta == null || categorieVarsta.isEmpty()) {
            throw new ServiceException("Date invalide!");
        }

        return inscriereDBRepo.getParticipantByProbaSiCategorie(idProba, categorieVarsta);
    }

    public void inscriereParticipant(String nume, String cnp, List<Long> probeIds) {
        if(probeIds.isEmpty())
            throw new ServiceException("selecteaza cel putin o proba!");

        if(probeIds.size() > 2)
            throw new ServiceException("maxim 2 probe!");

        Participant p = participantDBRepo.findByCnp(cnp);
        int varsta = calculeazaVarsta(cnp);

        if(p == null) {
            p = new Participant(nume, cnp, varsta);
            participantDBRepo.add(p);
            p = participantDBRepo.findByCnp(cnp);
        }

        int nr = inscriereDBRepo.countByParticipant(p.getId());
        if(nr + probeIds.size() > 2)
            throw new ServiceException("depaseste limita de 2 probe!");

        for(Long idProba: probeIds) {
            Proba proba = probaDBRepo.findById(idProba);

            if(!esteVarstaValida(varsta, proba.getCategorie()))
                throw new ServiceException("varsta invalida!");

            if(inscriereDBRepo.exists(p.getId(), idProba))
                throw new ServiceException("deja inscris!");

            Inscriere i = new Inscriere(p, proba);
            inscriereDBRepo.add(i);
        }
    }

    public void stergeProbaParticipant(String cnp, long idProba) {
        Participant p = participantDBRepo.findByCnp(cnp);
        if(p == null)
            throw new ServiceException("participantul cu cnp-ul dat nu exista!");

        Inscriere i = inscriereDBRepo.findByParticipantAndProba(p.getId(), idProba);
        if(i == null)
            throw new ServiceException("participantul nu este inscris la aceasta proba!");

        inscriereDBRepo.delete(i);
    }


    private boolean esteVarstaValida(int varsta, String categorie) {
        String[] parts = categorie.split("-");
        int min = Integer.valueOf(parts[0]);
        int max = Integer.valueOf(parts[1]);

        return varsta >= min && varsta <= max;
    }

    private int calculeazaVarsta(String cnp) {
        if (cnp.length() < 7) throw new ServiceException("CNP invalid!");

        int an = Integer.parseInt(cnp.substring(1, 3));
        int luna = Integer.parseInt(cnp.substring(3, 5));
        int zi = Integer.parseInt(cnp.substring(5, 7));

        char s = cnp.charAt(0);
        int secol;
        if (s == '1' || s == '2') secol = 1900;
        else if (s == '5' || s == '6') secol = 2000;
        else throw new ServiceException("CNP invalid!");

        int anNastere = secol + an;
        LocalDate dataNasterii = LocalDate.of(anNastere, luna, zi);
        LocalDate azi = LocalDate.now();

        int varsta = azi.getYear() - dataNasterii.getYear();
        if (azi.isBefore(dataNasterii.plusYears(varsta))) varsta--;

        return varsta;
    }
}
