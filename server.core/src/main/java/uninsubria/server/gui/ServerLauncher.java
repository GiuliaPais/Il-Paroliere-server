package uninsubria.server.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uninsubria.server.core.MasterServer;

/**
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ServerLauncher extends Application {
    /*---Fields---*/
    public static MasterServer masterServer = null;

    /*---Constructors---*/

    /*---Methods---*/
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainServerGui.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
