package uninsubria.server.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uninsubria.server.core.MasterServer;
import java.util.prefs.Preferences;

/**
 * Entry point of the server-side application. Instantiates the GUI.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ServerLauncher extends Application {
    /*---Fields---*/
    public static MasterServer masterServer = null;
    public static Preferences prefs;

    /*---Constructors---*/

    /*---Methods---*/
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.prefs = Preferences.userRoot().node("IlParoliere-server");
        masterServer = new MasterServer();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DbLogin.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void newServerInstance() {
        MasterServer newServer = new MasterServer(masterServer);
        masterServer = newServer;
    }

    public static void main(String[] args) {
        launch();
    }
}
