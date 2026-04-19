package client.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Operator;
import services.IServices;


public class LoginController {
    private IServices service;

    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Label lblError;
    @FXML
    private Button btnLogin;

    public void setService(IServices service) {
        this.service = service;
    }

    @FXML
    private void onLogin() {
        btnLogin.setDisable(true);
        lblError.setText("");

        String username = tfUsername.getText();
        String password = pfPassword.getText();

        Operator op = new Operator(username, password);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            loader.load();
            MainController mainController = loader.getController();
            mainController.setOperator(op);

            Task<Void> loginTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    service.login(op, mainController); // blocking, correct observer
                    return null;
                }
            };

            loginTask.setOnSucceeded(evt -> {
                mainController.setService(service);
                Stage stage = new Stage();
                stage.setTitle("Aplicatie inscrieri - " + op.getUsername());
                stage.setScene(new Scene(loader.getRoot()));
                stage.setWidth(850);
                stage.setHeight(500);
                stage.show();
                tfUsername.getScene().getWindow().hide();
            });

            loginTask.setOnFailed(evt -> {
                Throwable ex = loginTask.getException();
                Platform.runLater(() -> {
                    lblError.setText(ex != null && ex.getMessage() != null
                            ? ex.getMessage() : "Eroare la login!");
                    btnLogin.setDisable(false);
                });
            });

            Thread t = new Thread(loginTask);
            t.setDaemon(true);
            t.start();

        } catch (Exception e) {
            lblError.setText("Eroare la incarcare fereastra!");
            btnLogin.setDisable(false);
        }
    }
}
