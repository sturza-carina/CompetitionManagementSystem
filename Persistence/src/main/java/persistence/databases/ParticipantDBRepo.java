package persistence.databases;

import model.Participant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import persistence.DBUtils;
import persistence.interfaces.IParticipantRepository;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParticipantDBRepo implements IParticipantRepository {

    private static final Logger logger = LogManager.getLogger(ParticipantDBRepo.class);
    private DBUtils dbUtils;

    public ParticipantDBRepo(DBUtils dbUtils) {
        logger.info("Initializing ParticipantDBRepo: ");
        this.dbUtils =  dbUtils ;
    }


    @Override
    public Participant findByCnp(String cnp) {
        String sql="SELECT * FROM participanti WHERE cnp = ?";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)){

            ps.setString(1,cnp);

            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                long id = rs.getLong("id_participant");
                String nume =  rs.getString("nume");
                String _cnp = rs.getString("cnp");
                int varsta = rs.getInt("varsta");

                Participant p= new Participant(nume, _cnp,varsta);

                p.setId(id);

                return p;
            }

        }catch(SQLException e){
            logger.error(e);
        }

        return null;
    }

    @Override
    public void add(Participant elem) {
        logger.traceEntry("saving participant {} " + elem);

        String sql = "INSERT INTO participanti (nume, cnp, varsta) VALUES (?, ?, ?)";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, elem.getNume());
            ps.setString(2, elem.getCnp());
            ps.setInt(3, elem.getVarsta());

            int result = ps.executeUpdate();
            logger.traceExit("saved {} instances ", result);

        } catch(SQLException e){
            logger.error(e);
            System.err.println("Error db add: " + e);
        }

        logger.traceExit();
    }

    @Override
    public void delete(Participant elem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Participant elem, Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Participant findById(Long id) {
        String sql = "SELECT * FROM participanti WHERE id_participant = ?";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)){

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                String nume = rs.getString("nume");
                String cnp = rs.getString("cnp");
                int varsta = rs.getInt("varsta");

                Participant p = new Participant(nume, cnp, varsta);
                p.setId(id);

                return p;
            }

        } catch(SQLException e){
            logger.error(e);
        }

        return null;
    }

    @Override
    public Iterable<Participant> findAll() {
        return getAll();
    }

    @Override
    public Collection<Participant> getAll() {
        List<Participant> list = new ArrayList<>();

        String sql="SELECT * FROM participanti";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs=ps.executeQuery()){

            while(rs.next()){
                long id = rs.getLong("id_participant");
                String nume = rs.getString("nume");
                String cnp = rs.getString("cnp");
                int varsta = rs.getInt("varsta");

                Participant p = new Participant(nume, cnp, varsta);
                p.setId(id);

                list.add(p);
            }

        }catch(SQLException e){
            logger.error(e);
        }

        return list;
    }
}
