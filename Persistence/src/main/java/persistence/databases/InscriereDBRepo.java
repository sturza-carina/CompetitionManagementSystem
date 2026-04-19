package persistence.databases;

import model.Inscriere;
import model.Participant;
import model.Proba;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import persistence.DBUtils;
import persistence.interfaces.IInscriereRepository;
import persistence.interfaces.IParticipantRepository;
import persistence.interfaces.IProbaRepository;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InscriereDBRepo implements IInscriereRepository {

    private static final Logger logger = LogManager.getLogger(InscriereDBRepo.class);
    private DBUtils dbUtils;

    private IParticipantRepository participantRepo;
    private IProbaRepository probaRepo;

    public InscriereDBRepo(DBUtils dbUtils, IParticipantRepository participantRepo, IProbaRepository probaRepo) {
        logger.info("Initializing InscriereDBRepo: ");
        this.dbUtils = dbUtils;
        this.participantRepo = participantRepo;
        this.probaRepo = probaRepo;
    }


    @Override
    public Iterable<Inscriere> findByParticipant(Long idParticipant) {
        List<Inscriere> list = new ArrayList<>();

        String sql = "SELECT * FROM inscrieri WHERE id_participant = ?";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)){

            ps.setLong(1,idParticipant);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                long id = rs.getLong("id");
                long id_participant = rs.getLong("id_participant");
                long id_proba = rs.getLong("id_proba");

                Participant participant = participantRepo.findById(id_participant);
                Proba proba = probaRepo.findById(id_proba);

                Inscriere i = new  Inscriere(participant,proba);

                i.setId(id);

                list.add(i);
            }

        }catch(SQLException e){
            logger.error(e);
        }

        return list;
    }

    @Override
    public Iterable<Inscriere> findByProba(Long idProba) {
        List<Inscriere> list = new ArrayList<>();

        String sql="SELECT * FROM inscrieri WHERE id_proba=?";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)){

            ps.setLong(1,idProba);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                long id = rs.getLong("id");
                long id_participant = rs.getLong("id_participant");
                long id_proba = rs.getLong("id_proba");

                Participant participant = participantRepo.findById(id_participant);
                Proba proba = probaRepo.findById(id_proba);

                Inscriere i = new  Inscriere(participant,proba);

                i.setId(id);

                list.add(i);
            }

        }catch(SQLException e){
            logger.error(e);
        }

        return list;
    }

    @Override
    public int countByProba(Long idProba) {
        String sql = "SELECT COUNT(*) AS cnt FROM inscrieri WHERE id_proba=?";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)){

            ps.setLong(1,idProba);

            ResultSet rs = ps.executeQuery();

            if(rs.next())
                return rs.getInt("cnt");

        }catch(SQLException e){
            logger.error(e);
        }

        return 0;
    }

    @Override
    public int countByParticipant(Long idParticipant) {
        String sql = "SELECT COUNT(*) AS cnt FROM inscrieri WHERE id_participant=?";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)){

            ps.setLong(1,idParticipant);

            ResultSet rs = ps.executeQuery();

            if(rs.next())
                return rs.getInt("cnt");

        }catch(SQLException e){
            logger.error(e);
        }

        return 0;
    }

    @Override
    public Inscriere findByParticipantAndProba(Long idParticipant, Long idProba) {
        String sql = """
                SELECT *
                FROM inscrieri WHERE id_participant = ? AND id_proba = ?;
                """;
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1,idParticipant);
            ps.setLong(2,idProba);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                long id = rs.getLong("id");
                long id_participant = rs.getLong("id_participant");
                long id_proba = rs.getLong("id_proba");

                Participant participant = participantRepo.findById(id_participant);
                Proba proba = probaRepo.findById(id_proba);

                Inscriere i = new  Inscriere(participant,proba);
                i.setId(id);

                return  i;
            }

        } catch(SQLException e){
            logger.error(e);
        }
        return null;
    }

    @Override
    public boolean exists(Long idParticipant, Long idProba) {
        return findByParticipantAndProba(idParticipant, idProba) != null;
    }

    @Override
    public void add(Inscriere elem) {
        logger.traceEntry("saving inscriere {} " + elem);

        String sql = "INSERT INTO inscrieri (id_participant, id_proba) VALUES (?, ?)";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, elem.getParticipant().getId());
            ps.setLong(2, elem.getProba().getId());

            int result = ps.executeUpdate();
            logger.traceExit("saved {} instances ", result);

        } catch(SQLException e){
            logger.error(e);
            System.err.println("Error db add: " + e);
        }

        logger.traceExit();
    }

    @Override
    public void delete(Inscriere elem) {
        logger.traceEntry("deleting inscriere {}", elem);

        String sql = "DELETE FROM inscrieri WHERE id=?";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)){

            ps.setLong(1, elem.getId());

            int result = ps.executeUpdate();
            logger.trace("deleted {} instances", result);

        } catch(SQLException e){
            logger.error(e);
        }

        logger.traceExit();
    }

    @Override
    public void update(Inscriere elem, Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Inscriere findById(Long id) {
        logger.traceEntry("find inscriere with id {}", id);

        String sql = "SELECT * FROM inscrieri WHERE id=?";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)){

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                long id_participant = rs.getLong("id_participant");
                long id_proba = rs.getLong("id_proba");

                Participant participant = participantRepo.findById(id_participant);
                Proba proba = probaRepo.findById(id_proba);

                Inscriere inscriere = new Inscriere(participant, proba);
                inscriere.setId(id);

                logger.traceExit(inscriere);
                return inscriere;
            }

        } catch(SQLException e){
            logger.error(e);
        }

        logger.traceExit();
        return null;
    }

    @Override
    public Iterable<Inscriere> findAll() {
        return getAll();
    }

    @Override
    public Collection<Inscriere> getAll() {
        logger.traceEntry();

        List<Inscriere> list = new ArrayList<>();

        String sql = "SELECT * FROM inscrieri";
        Connection con = dbUtils.getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                long id = rs.getLong("id");
                long id_participant = rs.getLong("id_participant");
                long id_proba = rs.getLong("id_proba");

                Participant participant = participantRepo.findById(id_participant);
                Proba proba = probaRepo.findById(id_proba);

                Inscriere i = new Inscriere(participant, proba);
                i.setId(id);

                list.add(i);
            }

        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit(list);
        return list;
    }

    @Override
    public List<Participant> getParticipantByProbaSiCategorie(Long idProba, String categorieVarsta) {
        logger.traceEntry("find participants by proba {} and categorie {}", idProba, categorieVarsta);

        List<Participant> result = new ArrayList<>();

        String sql = "SELECT id_participant FROM inscrieri WHERE id_proba = ?";
        Connection con = dbUtils.getConnection();

        String[] parts = categorieVarsta.split("-");
        int min = Integer.parseInt(parts[0]);
        int max = Integer.parseInt(parts[1]);

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idProba);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long idParticipant = rs.getLong("id_participant");

                Participant p = participantRepo.findById(idParticipant);

                if (p != null && p.getVarsta() >= min && p.getVarsta() <= max) {
                    result.add(p);
                }
            }

        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit(result);
        return result;
    }
}
