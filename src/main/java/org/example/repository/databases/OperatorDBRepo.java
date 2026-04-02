package org.example.repository.databases;

import org.example.domain.Operator;
import org.example.repository.interfaces.IOperatorRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.example.utils.DBUtils;


public class OperatorDBRepo implements IOperatorRepository {

    private static final Logger logger = LogManager.getLogger(OperatorDBRepo.class);
    private DBUtils dbUtils;

    public OperatorDBRepo(DBUtils dbUtils) {
        logger.info("Initializing OperatorDBRepo: ");
        this.dbUtils =  dbUtils;
    }


    @Override
    public Operator findByUsername(String username) {
        logger.info("Searching for username: " + username);
        String sql = "SELECT * FROM operatori WHERE username = ?";
        Connection conn = dbUtils.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                long id = rs.getInt("id_operator");
                String user_name = rs.getString("username");
                String password = rs.getString("password");

                Operator op = new Operator(user_name, password);
                op.setId(id);

                return op;
            }


        } catch(SQLException e){
            logger.error(e);
        }

        return null;
    }

    @Override
    public void add(Operator elem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Operator elem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Operator elem, Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Operator findById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<Operator> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Operator> getAll() {
        throw new UnsupportedOperationException();
    }
}
