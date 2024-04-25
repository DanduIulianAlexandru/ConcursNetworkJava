package concurs.clientfx;

import concurs.model.*;
import concurs.model.validators.ValidationException;
import concurs.persistence.config.DBConfig;
import concurs.services.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AppController implements Initializable, IConcursObserver {
    private IConcursServices server;
    private final Logger logger= LogManager.getLogger();
    private Admin loggedAdmin;
    @FXML
    private TableView<Proba> tabelProbe;
    @FXML
    private TableColumn<Proba, String> idProbaCell;
    @FXML
    private TableColumn<Proba, String> distantaCell;
    @FXML
    private TableColumn<Proba, String> stilCell;
    @FXML
    private TableColumn<Proba, String> nrParticipantiCell;

    @FXML
    private TableView<Participant> tabelParticipant;
    @FXML
    private TableColumn<Participant, String> numeCell;
    @FXML
    private TableColumn<Participant, String> varstaCell;
    @FXML
    private TableColumn<Participant, String> listaProbeCell;

    @FXML
    private TextField numeField;
    @FXML
    private TextField varstaField;
    @FXML
    private TextField probeField;
    @FXML
    private TextField cautareField;
    @FXML
    private Button inscrieButton;
    @FXML
    private Button cautaButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label infoLabel;
    @FXML
    private Label errorLabel;
    private ObservableList<Participant> participantObservableList;


    public AppController(){

    }

    public void setAdmin(Admin admin){
        loggedAdmin = admin;
    }

    public void setServer(IConcursServices s){
        server = s;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idProbaCell.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getId().toString()));
        distantaCell.setCellValueFactory(c-> new SimpleStringProperty
                (c.getValue().getDistanta().toString().substring(1) + "M"));
        stilCell.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getStil().toString()));

        numeCell.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getNume()));
        varstaCell.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getVarsta().toString()));
        listaProbeCell.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getProbe().toString()));
        nrParticipantiCell.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNrParticipanti().toString()));
    }

    public void init(){
        try{
            tabelProbe.getItems().clear();
            tabelParticipant.getItems().clear();
            ObservableList<Proba> probeObservableList = tabelProbe.getItems();
            probeObservableList.addAll(server.getProbe().stream().sorted(
                    Comparator.comparingInt(Proba::getId)
            ).toList());
            tabelProbe.setItems(probeObservableList);

            participantObservableList = tabelParticipant.getItems();
            participantObservableList.addAll(server.getParticipanti().stream().sorted(
                    Comparator.comparing(Participant::getNume)
            ).toList());
            tabelParticipant.setItems(participantObservableList);
        }
        catch (ConcursException e){
            errorLabel.setText(e.getMessage());
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    public void onInscrieClicked(){
        String nume = numeField.getText();
        String varsta = varstaField.getText();
        String probeS = probeField.getText();

        if (nume.equals("") || varsta.equals("") || probeS.equals("")){
            errorLabel.setText("Campurile nu pot fi empty");
            return;
        }

        if (!isNumeric(varsta)){
            errorLabel.setText("Varsta trebuie sa fie un numar");
            return;
        }
        List<Integer> probe = new ArrayList<>();
        String[] parts = probeS.split("\u0020", 0);
        for(String part : parts){
            if (!isNumeric(part)){
                System.out.println(part);
                errorLabel.setText("Formatul pentru probe nu e corect");
                probeField.setText("");
                return;
            }
            try {
                if (server.getProba(Integer.parseInt(part)) == null) {
                    errorLabel.setText("Nu exista proba cu id-ul " + part);
                    probeField.setText("");
                    return;
                }
            }catch (ConcursException e){
                errorLabel.setText(e.getMessage());
            }
            probe.add(Integer.parseInt(part));
        }

        try{
            Participant p = new Participant(nume, Integer.parseInt(varsta), probe);
            p.setId(0);
            System.out.println(errorLabel.toString());
            server.addParticipant(p);
            numeField.setText("");
            varstaField.setText("");
            probeField.setText("");
            errorLabel.setText("Participant adaugat cu succes");


        }catch (ConcursException e){
            errorLabel.setText(e.toString().substring(59));
            logger.error(e.toString());
            numeField.setText("");
            varstaField.setText("");
            probeField.setText("");
            errorLabel.setText(e.getMessage());
        }

    }

    public void onCautaClicked() {
        String searchString = cautareField.getText();
        if (searchString.equals("")){
            tabelParticipant.getItems().clear();
            ObservableList<Participant> participantObservableList = tabelParticipant.getItems();
            try {
                participantObservableList.addAll(server.getParticipanti().stream().sorted(
                        Comparator.comparing(Participant::getNume)
                ).toList());
            } catch (ConcursException e) {
                errorLabel.setText(e.getMessage());
            }
            tabelParticipant.setItems(participantObservableList);
        }
        try{
            tabelParticipant.getItems().clear();
            ObservableList<Participant> participantObservableList = tabelParticipant.getItems();
            participantObservableList.addAll(server.getParticipantiByString(searchString).stream().sorted(
                    Comparator.comparing(Participant::getNume)
            ).toList());
            tabelParticipant.setItems(participantObservableList);
        }
        catch (ConcursException e){
            errorLabel.setText(e.getMessage());
        }

        cautareField.clear();
    }

    public void onLogoutClicked(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log out");

        alert.setContentText("Press OK if you want to log out");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isEmpty()){
            System.out.println("Logout canceled");
        }else if(result.get() == ButtonType.CANCEL){
            System.out.println("Logout canceled");
        }
        else if(result.get() == ButtonType.OK){
            logger.info("Admin logged out");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();

            FXMLLoader appLoader = new FXMLLoader(getClass().getClassLoader().getResource("app.fxml"));
            Parent appRoot = appLoader.load();

            loginController.setAppController(appLoader.getController());
            loginController.setServer(server);
            loginController.setParent(appRoot);

            try{
                server.logout(loggedAdmin, this);
            }catch (ConcursException e){
                errorLabel.setText(e.getMessage());
            }
            System.out.println("Login Button pressed");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login for admins");
            stage.show();
        }
    }

    @Override
    public void participantAdded(Participant participant) {
        Platform.runLater(this::init);
    }
}
