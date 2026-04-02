package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.controller.dto.ProbaDTO;
import org.example.domain.Operator;
import org.example.domain.Participant;
import org.example.domain.Proba;
import org.example.exceptions.ServiceException;
import org.example.service.Service;

import java.util.List;
import java.util.stream.Collectors;

public class MainController {
    private Service service;
    private Operator op;
    private ProbaDTO selected = null;

    @FXML
    private TableView<ProbaDTO> tableProbe;
    @FXML
    private TableColumn<ProbaDTO, Boolean> colSelect;
    @FXML
    private TableColumn<ProbaDTO, String> numeProba;
    @FXML
    private TableColumn<ProbaDTO, String> categorie;
    @FXML
    private TableColumn<ProbaDTO, Integer> nrInscrisi;

    @FXML
    private TableView<Participant> tableParticipanti;
    @FXML
    private TableColumn<Participant, String> numeParticipant;
    @FXML
    private TableColumn<Participant, Integer> varsta;

    @FXML
    private TextField txtNume;
    @FXML
    private TextField txtCnp;

    @FXML
    private Label lblError;

    private ObservableList<ProbaDTO> probeModel = FXCollections.observableArrayList();
    private ObservableList<Participant> participantiModel = FXCollections.observableArrayList();

    public void setService(Service service) {
        this.service = service;

        loadProbe();
    }

    public void setOperator(Operator operator) {
        this.op = operator;
    }

    @FXML
    public void initialize() {
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            private ProbaDTO currentProba = null;

            {
                checkBox.setOnAction(e -> {
                    if (currentProba == null) return;

                    long nrSelectate = probeModel.stream()
                            .filter(ProbaDTO::isSelected)
                            .count();

                    if (currentProba.isSelected() && nrSelectate > 2) {
                        currentProba.setSelected(false);
                        checkBox.setSelected(false);
                        lblError.setText("maxim 2 probe!");
                    } else {
                        lblError.setText("");
                        if (currentProba.isSelected()) {
                            selected = currentProba;
                            loadParticipanti(selected);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (currentProba != null) {
                    checkBox.selectedProperty().unbindBidirectional(currentProba.selectedProperty());
                }

                if (empty) {
                    currentProba = null;
                    setGraphic(null);
                } else {
                    currentProba = getTableView().getItems().get(getIndex());
                    checkBox.selectedProperty().bindBidirectional(currentProba.selectedProperty());
                    setGraphic(checkBox);
                }
            }
        });

        numeProba.setCellValueFactory(new PropertyValueFactory<>("nume"));
        categorie.setCellValueFactory(new PropertyValueFactory<>("categorieVarsta"));
        nrInscrisi.setCellValueFactory(new PropertyValueFactory<>("nrInscrisi"));

        numeParticipant.setCellValueFactory(new PropertyValueFactory<>("nume"));
        varsta.setCellValueFactory(new PropertyValueFactory<>("varsta"));

        tableProbe.setItems(probeModel);
        tableParticipanti.setItems(participantiModel);

        tableProbe.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) loadParticipanti(newVal);
                });
    }

    private void loadProbe() {
        probeModel.clear();
        service.getAllProbe().forEach(p -> {
            long nrInscrisi = service.getAllInscrieri().stream()
                    .filter(i -> i.getProba().getId() == p.getId())
                    .count();
            probeModel.add(new ProbaDTO(p.getId(), p.getNume(), p.getCategorie(), nrInscrisi));
        });
    }


    private void loadParticipanti(ProbaDTO proba) {
        participantiModel.clear();
        List<Participant> participanti = service.getParticipantByProbaSiCategorie(proba.getId(), proba.getCategorieVarsta());
        participantiModel.addAll(participanti);
    }


    @FXML
    private void onInscriere() {
        List<Long> probeSelectate = probeModel.stream()
                .filter(ProbaDTO::isSelected)
                .map(ProbaDTO::getId)
                .collect(Collectors.toList());

        if (probeSelectate.isEmpty()) {
            lblError.setText("selectați cel puțin o probă!");
            return;
        }

        String nume = txtNume.getText();
        String cnp = txtCnp.getText();

        if (nume.isEmpty() || cnp.isEmpty()) {
            lblError.setText("completați numele si CNP-ul!");
            return;
        }

        try {
            service.inscriereParticipant(nume, cnp, probeSelectate);
            lblError.setText("");

            ProbaDTO proba = selected;

            probeModel.forEach(p -> p.setSelected(false));
            selected = null;

            loadProbe();
            txtNume.clear();
            txtCnp.clear();

            if (proba != null) {
                ProbaDTO probaNoua = probeModel.stream()
                        .filter(p -> p.getId() == proba.getId())
                        .findFirst()
                        .orElse(null);
                if (probaNoua != null) {
                    loadParticipanti(probaNoua);
                }
            }
        } catch (ServiceException e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    private void onSterge() {
        ProbaDTO proba = tableProbe.getSelectionModel().getSelectedItem();
        Participant participant = tableParticipanti.getSelectionModel().getSelectedItem();

        if (proba == null || participant == null) {
            lblError.setText("selectati o proba si un participant!");
            return;
        }

        try {
            service.stergeProbaParticipant(participant.getCnp(), proba.getId());
            lblError.setText("");

            long idProba = proba.getId();
            loadProbe();

            ProbaDTO probaNoua = probeModel.stream()
                    .filter(p -> p.getId() == idProba)
                    .findFirst()
                    .orElse(null);

            if (probaNoua != null) {
                tableProbe.getSelectionModel().select(probaNoua); // 🔥 IMPORTANT
                loadParticipanti(probaNoua);
            } else {
                participantiModel.clear();
            }


        } catch (ServiceException e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    private void onLogout() {
        tableProbe.getScene().getWindow().hide();
    }

}

