package concurs.clientfx;


import concurs.model.Admin;
import concurs.model.validators.ParticipantValidator;
import concurs.model.validators.ProbaValidator;
import concurs.persistence.config.DBConfig;
import concurs.persistence.jdbc.DBRepositoryParticipant;
import concurs.persistence.jdbc.DBRepositoryProba;
import concurs.services.IConcursServices;
import concurs.services.ConcursException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class LoginController {
    private final Logger logger = LogManager.getLogger();
    private IConcursServices server;
    private AppController appController;
    Parent mainAppParent;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button exitButton;
    @FXML
    private Label errorLabel;

    public LoginController() {

    }

    public void setServer(IConcursServices s) {
        server = s;
    }

    public void setParent(Parent p) {
        mainAppParent = p;
    }

    public void setAppController(AppController a){
        appController = a;
    }

    public void initialize() {
        errorLabel.setOpacity(0);
    }

    @FXML
    public void onLoginButtonClicked(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.equals("") || password.equals("")) {
            errorLabel.setOpacity(100);
            errorLabel.setText("Username or password cannot be empty");
            usernameField.clear();
            passwordField.clear();
            return;
        }

        try{
            Admin admin = new Admin(username, password);
            admin.setId(0);
            admin = server.login(admin, appController);
            logger.info("Admin " + username + " logged");
            switchToApp(event, admin);
        } catch (ConcursException e) {
            usernameField.clear();
            passwordField.clear();
            errorLabel.setText(e.getMessage());
            errorLabel.setOpacity(100);
        }

    }

    protected void switchToApp(ActionEvent event, Admin admin) throws IOException {
        appController.setAdmin(admin);
        appController.setServer(server);
        appController.init();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(mainAppParent);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("App for admin " + admin.getUsername());
        stage.show();
    }


    @FXML
    public void onExitButtonClicked() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

}
