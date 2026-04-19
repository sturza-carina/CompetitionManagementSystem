package persistence.databases;

import model.Proba;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import persistence.DBUtils;
import persistence.interfaces.IProbaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ProbaDBRepo implements IProbaRepository {

    private static final Logger logger = LogManager.getLogger(ProbaDBRepo.class);
    private DBUtils dbUtils;

    public ProbaDBRepo(DBUtils dbUtils) {
        logger.info("Initializing ProbaDBRepo: ");
        this.dbUtils = dbUtils;
    }


    @Override
    public Iterable<Proba> findByCategorieVarsta(String categorieVarsta) {
        List<Proba> list=new ArrayList<>();

        String sql="SELECT * FROM probe WHERE categorie_varsta = ?";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps=con.prepareStatement(sql)){

            ps.setString(1,categorieVarsta);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                long id = rs.getLong("id_proba");
                String nume = rs.getString("nume");
                String categorie_varsta = rs.getString("categorie_varsta");

                Proba p = new Proba(nume, categorie_varsta);

                p.setId(id);
                list.add(p);
            }

        }catch(SQLException e){
            logger.error(e);
        }

        return list;
    }

    @Override
    public void add(Proba elem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Proba elem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Proba elem, Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Proba findById(Long id) {
        String sql = "SELECT * FROM probe WHERE id_proba = ?";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql)){

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                String nume = rs.getString("nume");
                String categorie = rs.getString("categorie_varsta");

                Proba p = new Proba(nume, categorie);
                p.setId(id);

                return p;
            }

        } catch(SQLException e){
            logger.error(e);
        }

        return null;
    }

    @Override
    public Iterable<Proba> findAll() {
        return getAll();
    }

    @Override
    public Collection<Proba> getAll() {
        List<Proba> list = new ArrayList<>();

        String sql="SELECT * FROM probe";
        Connection con = dbUtils.getConnection();

        try(PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs=ps.executeQuery()){

            while(rs.next()){
                long id  = rs.getLong("id_proba");
                String nume = rs.getString("nume");
                String categorie = rs.getString("categorie_varsta");

                Proba p = new Proba(nume, categorie);
                p.setId(id);

                list.add(p);
            }

        }catch(SQLException e){
            logger.error(e);
        }

        return list;
    }
}
