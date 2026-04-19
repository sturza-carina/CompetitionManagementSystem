package client.gui;

import client.gui.dto.ProbaDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Inscriere;
import model.Operator;
import model.Participant;
import model.Proba;
import services.IObserver;
import services.IServices;
import services.InscriereException;


import java.util.List;
import java.util.stream.Collectors;

public class MainController implements IObserver {
    private IServices service;
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

    public void setService(IServices service) {
        this.service = service;

        loadProbeAsync();
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
                            tableProbe.getSelectionModel().select(currentProba);
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
        nrInscrisi.setCellValueFactory(cellData ->
                cellData.getValue().nrInscrisiProperty().asObject());

        numeParticipant.setCellValueFactory(new PropertyValueFactory<>("nume"));
        varsta.setCellValueFactory(new PropertyValueFactory<>("varsta"));

        tableProbe.setItems(probeModel);
        tableParticipanti.setItems(participantiModel);

        tableProbe.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selected = newVal;
                        loadParticipanti(newVal);
                    }
                });
    }


    private void loadParticipanti(ProbaDTO proba) {
        try {
            participantiModel.clear();
            List<Participant> list =
                    service.getParticipantByProbaSiCategorie(proba.getId(), proba.getCategorieVarsta());
            participantiModel.addAll(list);

        } catch (InscriereException e) {
            lblError.setText(e.getMessage());
        }
    }

    private void loadProbeAsync() {
        Task<Void> task = new Task<>() {
            private List<Proba> probe;
            private List<Inscriere> inscrieri;

            @Override
            protected Void call() throws Exception {
                probe = service.getAllProbe();
                inscrieri = service.getAllInscrieri();
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    long selectedId = selected != null ? selected.getId() : -1;

                    if (probeModel.isEmpty()) {
                        // prima incarcare - construieste lista normal
                        for (Proba p : probe) {
                            long nr = inscrieri.stream()
                                    .filter(i -> i.getProba().getId() == p.getId())
                                    .count();
                            probeModel.add(new ProbaDTO(p.getId(), p.getNume(), p.getCategorie(), nr));
                        }
                    } else {
                        // actualizari ulterioare — doar updateaza nrInscrisi, pastreaza selected
                        for (ProbaDTO dto : probeModel) {
                            long nr = inscrieri.stream()
                                    .filter(i -> i.getProba().getId() == dto.getId())
                                    .count();
                            dto.setNrInscrisi(nr);
                        }
                    }

                    // re-sincronizeaza selected cu obiectul din lista
                    if (selectedId != -1) {
                        selected = probeModel.stream()
                                .filter(p -> p.getId() == selectedId)
                                .findFirst()
                                .orElse(null);

                        if (selected != null) {
                            loadParticipantiAsync(selected);
                        }
                    } else {
                        // fallback: verifica daca e ceva selectat in tabel
                        ProbaDTO tableSelected = tableProbe.getSelectionModel().getSelectedItem();
                        if (tableSelected != null) {
                            loadParticipantiAsync(tableSelected);
                        }
                    }

                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> lblError.setText("Eroare la încarcarea probelor!"));
            }
        };

        new Thread(task).start();
    }

    private void loadParticipantiAsync(ProbaDTO proba) {
        Task<List<Participant>> task = new Task<>() {
            @Override
            protected List<Participant> call() throws Exception {
                return service.getParticipantByProbaSiCategorie(proba.getId(), proba.getCategorieVarsta());
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> participantiModel.setAll(getValue()));
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> lblError.setText("Eroare la încarcarea participantilor!"));
            }
        };

        new Thread(task).start();
    }


    @FXML
    private void onInscriere() {
        List<Long> probeSelectate = probeModel.stream()
                .filter(ProbaDTO::isSelected)
                .map(ProbaDTO::getId)
                .collect(Collectors.toList());

        if (probeSelectate.isEmpty()) {
            lblError.setText("selectați cel puțin o proba!");
            return;
        }

        String nume = txtNume.getText();
        String cnp = txtCnp.getText();

        if (nume.isEmpty() || cnp.isEmpty()) {
            lblError.setText("completați numele si CNP-ul!");
            return;
        }


        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                service.inscriereParticipant(nume, cnp, probeSelectate);
                return null;
            }
        };

        task.setOnSucceeded(evt -> Platform.runLater(() -> {
            lblError.setText("");

            // reset selecții
            probeModel.forEach(p -> p.setSelected(false));
            txtNume.clear();
            txtCnp.clear();

        }));

        task.setOnFailed(evt -> Platform.runLater(() -> {
            lblError.setText(task.getException().getMessage());
        }));

        new Thread(task).start();

    }

    @FXML
    private void onSterge() {
        ProbaDTO proba = tableProbe.getSelectionModel().getSelectedItem();
        Participant participant = tableParticipanti.getSelectionModel().getSelectedItem();

        if (proba == null || participant == null) {
            lblError.setText("selectati o proba si un participant!");
            return;
        }

        long idProba = proba.getId();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                service.stergeProbaParticipant(participant.getCnp(), idProba);
                return null;
            }
        };

        task.setOnSucceeded(evt -> Platform.runLater(() -> {
            lblError.setText("");
        }));

        task.setOnFailed(evt -> Platform.runLater(() ->
                lblError.setText(task.getException().getMessage())
        ));

        new Thread(task).start();
    }

    @FXML
    private void onLogout() {
        try {
            service.logout(op, this);
        } catch (InscriereException e) {
            lblError.setText(e.getMessage());
        }

        tableProbe.getScene().getWindow().hide();
    }

    @Override
    public void inscriereAdded(Inscriere inscriere) throws InscriereException {
        Platform.runLater(() -> {
            long idProba = inscriere.getProba().getId();

            for (ProbaDTO dto : probeModel) {
                if (dto.getId() == idProba) {
                    dto.setNrInscrisi(dto.getNrInscrisi() + 1);
                    break;
                }
            }

            if (selected != null && selected.getId() == idProba) {
                participantiModel.add(inscriere.getParticipant());
            }
        });
    }

    @Override
    public void inscriereDeleted(Inscriere inscriere) throws InscriereException {
        Platform.runLater(() -> {
            long idProba = inscriere.getProba().getId();

            for (ProbaDTO dto : probeModel) {
                if (dto.getId() == idProba) {
                    dto.setNrInscrisi(dto.getNrInscrisi() - 1);
                    break;
                }
            }

            if (selected != null && selected.getId() == idProba) {
                participantiModel.removeIf(p ->
                        p.getCnp().equals(inscriere.getParticipant().getCnp()));
            }
        });
    }
}

