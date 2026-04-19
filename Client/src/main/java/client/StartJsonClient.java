package client;

import client.gui.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.jsonprotocol.ServicesJsonProxy;
import services.IServices;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartJsonClient extends Application {
    private static int defaultPort = 55555;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties props = new Properties();

        try {
            props.load(getClass().getResourceAsStream("/client.properties"));

        } catch (IOException e) {
            System.out.println("Cannot find client.properties");
            System.out.println(new File(".").getAbsolutePath());
            return;
        }

        String serverIP = props.getProperty("server.host", defaultServer);

        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(props.getProperty("server.port"));
        } catch (Exception e) {
            System.out.println("Using default port " + defaultPort);
        }

        IServices server = new ServicesJsonProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/login-view.fxml")
        );

        Parent root = loader.load();

        LoginController ctrl = loader.getController();
        ctrl.setService(server);


        primaryStage.setTitle("Concurs");
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(350);
        primaryStage.setHeight(300);
        primaryStage.show();
    }
}
