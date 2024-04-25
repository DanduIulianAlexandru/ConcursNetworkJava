package concurs.clientfx;

import concurs.networking.protobuffprotocol.ProtoConcursProxy;
import concurs.networking.rpcprotocol.ConcursServicesRpcProxy;
import concurs.services.IConcursServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartRpcClientFx extends Application {
    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("In start");
        String serverIP = defaultServer;
        int serverPort = defaultChatPort;
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IConcursServices server = new ConcursServicesRpcProxy(serverIP, serverPort);
        //IConcursServices server = new ProtoConcursProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("login.fxml"));
        Parent root = loader.load();

        LoginController loginCtrl = loader.getController();
        loginCtrl.setServer(server);

        FXMLLoader appLoader = new FXMLLoader(getClass().getClassLoader().getResource("app.fxml"));
        Parent appRoot = appLoader.load();

        AppController appCtrl = appLoader.getController();
        appCtrl.setServer(server);

        loginCtrl.setAppController(appCtrl);
        loginCtrl.setParent(appRoot);

        stage.setTitle("Login for admins");
        stage.setScene(new Scene(root, 600, 400));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
