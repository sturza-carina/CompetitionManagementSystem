package org.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtils {

    private Properties props = new Properties();
    private static final Logger logger = LogManager.getLogger(DBUtils.class);

    private Connection instance = null;

    public DBUtils(Properties properties) {
        props = properties;
    }

    private Connection getNewConnection(){
        logger.traceEntry();

        String url = props.getProperty("jdbc.url");
        String user = props.getProperty("jdbc.user");
        String pass = props.getProperty("jdbc.pass");

        logger.info("Trying to connect to database {}", url);

        Connection con = null;

        try {
            if(user != null && pass != null)
                con = DriverManager.getConnection(url,user,pass);
            else
                con = DriverManager.getConnection(url);

        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit(con);
        return con;
    }

    public Connection getConnection(){
        logger.traceEntry();

        try {
            if(instance == null || instance.isClosed())
                instance = getNewConnection();

        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit(instance);
        return instance;
    }

    public void closeConnection() {
        logger.traceEntry();

        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                logger.info("Connection closed");
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit();
    }

}
