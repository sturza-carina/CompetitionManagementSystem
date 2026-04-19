package server;

import model.Inscriere;
import model.Operator;
import model.Participant;
import model.Proba;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import persistence.databases.InscriereDBRepo;
import persistence.databases.OperatorDBRepo;
import persistence.databases.ParticipantDBRepo;
import persistence.databases.ProbaDBRepo;
import persistence.interfaces.IInscriereRepository;
import persistence.interfaces.IOperatorRepository;
import persistence.interfaces.IParticipantRepository;
import persistence.interfaces.IProbaRepository;
import services.IObserver;
import services.IServices;
import services.InscriereException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicesImpl implements IServices {

    private IOperatorRepository operatorDBRepo;
    private IParticipantRepository participantDBRepo;
    private IInscriereRepository inscriereDBRepo;
    private IProbaRepository probaDBRepo;

    private Map<String, IObserver> loggedClients;
    private static Logger logger = LogManager.getLogger(ServicesImpl.class);


    public ServicesImpl(IOperatorRepository operatorDBRepo, IParticipantRepository participantDBRepo,
                        IInscriereRepository inscriereDBRepo, IProbaRepository probaDBRepo) {
        this.operatorDBRepo = operatorDBRepo;
        this.participantDBRepo = participantDBRepo;
        this.inscriereDBRepo = inscriereDBRepo;
        this.probaDBRepo = probaDBRepo;

        loggedClients = new ConcurrentHashMap<>();
    }


    @Override
    public synchronized void login(Operator operator, IObserver client) throws InscriereException {
        logger.info("Login attempt for: {}", operator.getUsername());
        Operator op = operatorDBRepo.findByUsername(operator.getUsername());
        logger.info("Found operator: {}", op);

        if (op == null) {
            throw new InscriereException("Authentification failed.");
        }

        logger.info("Checking password...");
        logger.info("Password from client: {}", operator.getPassword());
        logger.info("Password from DB: {}", op.getPassword());

        if( !BCrypt.checkpw(operator.getPassword(), op.getPassword())) {
            throw new InscriereException("Wrong password.");
        }

        logger.info("Password OK!");

        if(loggedClients.get(operator.getUsername()) != null){
            throw new InscriereException("Username is already logged in.");
        }

        loggedClients.put(operator.getUsername(), client);
        logger.info("Login successful for: {}", operator.getUsername());
    }

    @Override
    public void logout(Operator operator, IObserver client) throws InscriereException {
        IObserver localClient = loggedClients.remove(operator.getUsername());

        if (localClient == null) {
            throw new InscriereException("Operator not logged in.");
        }
    }

    @Override
    public List<Proba> getAllProbe() throws InscriereException {
        return (List<Proba>) probaDBRepo.findAll();
    }

    @Override
    public List<Inscriere> getAllInscrieri() throws InscriereException {
        return (List<Inscriere>) inscriereDBRepo.findAll();
    }

    @Override
    public List<Participant> getParticipantByProbaSiCategorie(long idProba, String categorieVarsta) throws InscriereException {
        return inscriereDBRepo.getParticipantByProbaSiCategorie(idProba, categorieVarsta);
    }

    @Override
    public synchronized void inscriereParticipant(String nume, String cnp, List<Long> probeIds) throws InscriereException {
        if(probeIds.isEmpty())
            throw new InscriereException("selecteaza cel putin o proba!");

        if(probeIds.size() > 2)
            throw new InscriereException("maxim 2 probe!");

        Participant p = participantDBRepo.findByCnp(cnp);
        int varsta = calculeazaVarsta(cnp);

        if(p == null) {
            p = new Participant(nume, cnp, varsta);
            participantDBRepo.add(p);
            p = participantDBRepo.findByCnp(cnp);
        }

        int nr = inscriereDBRepo.countByParticipant(p.getId());
        if(nr + probeIds.size() > 2)
            throw new InscriereException("depaseste limita de 2 probe!");

        for(Long idProba: probeIds) {
            Proba proba = probaDBRepo.findById(idProba);

            if(!esteVarstaValida(varsta, proba.getCategorie()))
                throw new InscriereException("varsta invalida!");

            if(inscriereDBRepo.exists(p.getId(), idProba))
                throw new InscriereException("deja inscris!");

            Inscriere i = new Inscriere(p, proba);
            inscriereDBRepo.add(i);

            notifyInscriereAdded(i);
        }
    }

    @Override
    public synchronized void stergeProbaParticipant(String cnp, long idProba) throws InscriereException {
        Participant p =  participantDBRepo.findByCnp(cnp);
        if(p == null)
            throw new InscriereException("participantul cu cnp-ul dat nu exista!");

        Inscriere i = inscriereDBRepo.findByParticipantAndProba(p.getId(), idProba);
        if(i == null)
            throw new InscriereException("participantul nu e inscris la aceasta proba!");

        inscriereDBRepo.delete(i);

        notifyInscriereDeleted(i);
    }

    @Override
    public Participant getParticipantById(long idParticipant) throws InscriereException {
        return participantDBRepo.findById(idParticipant);
    }

    @Override
    public Proba getProbaById(long idProba) throws InscriereException {
        return probaDBRepo.findById(idProba);
    }

    private final int defaultThreads = 3;
    private void notifyInscriereAdded(Inscriere inscriere) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreads);

        for (IObserver obs : loggedClients.values()) {
            executor.execute(() -> {
                try {
                    obs.inscriereAdded(inscriere);
                } catch (InscriereException e) {
                    logger.error("Error notifying client", e);
                }
            });
        }

        executor.shutdown();
    }

    private void notifyInscriereDeleted(Inscriere inscriere) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreads);

        for (IObserver obs : loggedClients.values()) {
            executor.execute(() -> {
                try {
                    obs.inscriereDeleted(inscriere);
                } catch (InscriereException e) {
                    logger.error(e);
                }
            });
        }

        executor.shutdown();
    }


    private boolean esteVarstaValida(int varsta, String categorie) {
        String[] parts = categorie.split("-");
        int min = Integer.valueOf(parts[0]);
        int max = Integer.valueOf(parts[1]);

        return varsta >= min && varsta <= max;
    }

    private int calculeazaVarsta(String cnp) throws InscriereException {
        if (cnp.length() < 7) throw new InscriereException("CNP invalid!");

        int an = Integer.parseInt(cnp.substring(1, 3));
        int luna = Integer.parseInt(cnp.substring(3, 5));
        int zi = Integer.parseInt(cnp.substring(5, 7));

        char s = cnp.charAt(0);
        int secol;
        if (s == '1' || s == '2') secol = 1900;
        else if (s == '5' || s == '6') secol = 2000;
        else throw new InscriereException("CNP invalid!");

        int anNastere = secol + an;
        LocalDate dataNasterii = LocalDate.of(anNastere, luna, zi);
        LocalDate azi = LocalDate.now();

        int varsta = azi.getYear() - dataNasterii.getYear();
        if (azi.isBefore(dataNasterii.plusYears(varsta))) varsta--;

        return varsta;
    }
}
