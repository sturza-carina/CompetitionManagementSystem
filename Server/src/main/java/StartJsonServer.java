import network.utils.AbstractServer;
import network.utils.JsonConcurrentServer;
import network.utils.ServerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistence.DBUtils;
import persistence.databases.InscriereDBRepo;
import persistence.databases.OperatorDBRepo;
import persistence.databases.ParticipantDBRepo;
import persistence.databases.ProbaDBRepo;
import persistence.interfaces.IInscriereRepository;
import persistence.interfaces.IOperatorRepository;
import persistence.interfaces.IParticipantRepository;
import persistence.interfaces.IProbaRepository;
import server.ServicesImpl;
import services.IServices;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartJsonServer {
    private static int port = 55555;
    private static Logger logger = LogManager.getLogger(StartJsonServer.class);

    public static void main(String[] args) {
        Properties serverProps = new Properties();

        try {
            serverProps.load(StartJsonServer.class.getResourceAsStream("/server.properties"));
            logger.info("Server properties set. {} ", serverProps);

        } catch (IOException e) {
            logger.error("Cannot find chatserver.properties "+e);
            logger.debug("Looking for file in " + (new File(".")).getAbsolutePath());
            return;
        }

        DBUtils dbUtilsOperator = new DBUtils(serverProps);
        DBUtils dbUtilsParticipant = new DBUtils(serverProps);
        DBUtils dbUtilsProba = new DBUtils(serverProps);
        DBUtils dbUtilsInscriere = new DBUtils(serverProps);

        IOperatorRepository operatorRepo = new OperatorDBRepo(dbUtilsOperator);
        IParticipantRepository participantRepo = new ParticipantDBRepo(dbUtilsParticipant);
        IProbaRepository probaRepo = new ProbaDBRepo(dbUtilsProba);
        IInscriereRepository inscriereRepo = new InscriereDBRepo(dbUtilsInscriere, participantRepo, probaRepo);

        IServices serviceImpl = new ServicesImpl(
                operatorRepo,
                participantRepo,
                inscriereRepo,
                probaRepo
        );

        int serverPort = port;
        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));

        } catch (NumberFormatException e) {
            logger.error("Wrong Port Number " + e.getMessage());
            logger.debug("Using default port " + port);
        }

        logger.debug("Starting server on port: " + serverPort);

        AbstractServer server = new JsonConcurrentServer(serverPort, serviceImpl);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down server, closing DB connections...");
            dbUtilsOperator.closeConnection();
            dbUtilsParticipant.closeConnection();
            dbUtilsProba.closeConnection();
            dbUtilsInscriere.closeConnection();
            logger.info("All DB connections closed.");
        }));

        try {
            server.start();
        } catch (ServerException e) {
            logger.error("Error starting the server " + e.getMessage());
        }

    }
}
