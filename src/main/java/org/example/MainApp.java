package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.controller.LoginController;
import org.example.repository.databases.InscriereDBRepo;
import org.example.repository.databases.OperatorDBRepo;
import org.example.repository.databases.ParticipantDBRepo;
import org.example.repository.databases.ProbaDBRepo;
import org.example.service.Service;
import org.example.utils.DBUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MainApp extends Application {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // configurare log4j
        Properties props = new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }

        MainApp.log.info("Starting application...");

        DBUtils dbUtils = new DBUtils(props);

        try {
            ParticipantDBRepo participantDBRepo = new ParticipantDBRepo(dbUtils);
            ProbaDBRepo probaDBRepo = new ProbaDBRepo(dbUtils);
            InscriereDBRepo inscriereDBRepo = new InscriereDBRepo(dbUtils,  participantDBRepo, probaDBRepo);
            OperatorDBRepo operatorDBRepo = new OperatorDBRepo(dbUtils);

            Service service = new Service(inscriereDBRepo, participantDBRepo, probaDBRepo, operatorDBRepo);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
                Scene loginScene = new Scene(loader.load());

                LoginController loginController = loader.getController();
                loginController.setService(service);

                Stage loginStage = new Stage();
                loginStage.setTitle("Login");
                loginStage.setScene(loginScene);
                loginStage.setWidth(350);
                loginStage.setHeight(300);
                loginStage.show();

                loginStage.setOnCloseRequest(event ->
                        loginStage.close());

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            log.error("Eroare fatala:  " + e.getMessage());

        } finally {
            dbUtils.closeConnection();
            log.info("Conexiune inchisa. Aplicatie oprita.");
        }

    }
}
