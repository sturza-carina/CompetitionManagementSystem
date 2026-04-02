package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.domain.Operator;
import org.example.exceptions.ServiceException;
import org.example.service.Service;

import java.io.IOException;

public class LoginController {
    private Service service;

    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Label lblError;

    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    private void onLogin() {
        String username = tfUsername.getText();
        String password = pfPassword.getText();

        try{
            Operator op =  service.login(username, password);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            Scene scene = new Scene(loader.load());

            MainController mainController = loader.getController();
            mainController.setService(service);
            mainController.setOperator(op);

            Stage stage = new Stage();
            stage.setTitle("Aplicație Concurs");
            stage.setScene(scene);

            stage.show();

            tfUsername.getScene().getWindow().hide();

        } catch (ServiceException e){
            lblError.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Eroare la deschiderea ferestrei!");
        }
    }
}
