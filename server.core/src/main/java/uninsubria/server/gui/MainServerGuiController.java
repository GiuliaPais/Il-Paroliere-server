package uninsubria.server.gui;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.Socket;

/**
 * Controller class for the main portion of the server side GUI,
 * mainly composed of a console and a few options to start/stop the server and exit.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class MainServerGuiController {
    /*---Fields---*/
    @FXML
    AnchorPane root;
    @FXML TextFlow console;
    @FXML
    JFXButton back_btn;

    /*---Constructors---*/
    public MainServerGuiController() {
    }

    /*---Methods---*/
    public void initialize() {
        ServerLauncher.masterServer.setControllerReference(this);
    }

    public void printToConsole(String msg, MessageType msgType) {
        String index = "[" + (console.getChildren().size() + 1) + "] ";
        Text message = new Text(index + msg + System.lineSeparator());
        message.setId(msgType.name().toLowerCase());
        console.getChildren().add(message);
    }

    @FXML
    void startServer() {
        if (ServerLauncher.masterServer.isAlive()) {
            printToConsole("Server already running", MessageType.WARNING);
            return;
        }
        if (ServerLauncher.masterServer.getState().equals(Thread.State.NEW) ||
                ServerLauncher.masterServer.getState().equals(Thread.State.RUNNABLE)) {
            ServerLauncher.masterServer.start();
            back_btn.setDisable(true);
            return;
        }
        ServerLauncher.newServerInstance();
        ServerLauncher.masterServer.start();
        back_btn.setDisable(true);
        return;
    }

    @FXML void exit() {
        Platform.exit();
        System.exit(0);
    }

    @FXML void stopServer() {
        if (!ServerLauncher.masterServer.isAlive()) {
            printToConsole("No server to stop", MessageType.WARNING);
            return;
        }
        ServerLauncher.masterServer.interrupt();
        /* Dummy client: necessary because of server socket blocked on listening */
        try {
            new Socket("localhost", CommHolder.SERVER_PORT);
        } catch (IOException e) {
            printToConsole(e.getStackTrace().toString(), MessageType.WARNING);
        }
        back_btn.setDisable(false);
    }

    @FXML void back() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DbLogin.fxml"));
        Parent parent = loader.load();
        root.getScene().setRoot(parent);
    }
}
