package uninsubria.server.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uninsubria.server.core.MasterServer;
import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Giulia Pais
 * @version 0.9.0
 */
public class MainServerGuiController {
    /*---Fields---*/
    @FXML TextFlow console;
    @FXML Button serverStart_btn, stopServer_btn, exit_btn;

    /*---Constructors---*/
    public MainServerGuiController() {
        ServerLauncher.masterServer = new MasterServer(this);
    }

    /*---Methods---*/
    public void initialize() {
    }

    public void printToConsole(String msg, MessageType msgType) {
        String index = "[" + (console.getChildren().size() + 1) + "] ";
        Text message = new Text(index + msg + System.lineSeparator());
        message.setId(msgType.name().toLowerCase());
        console.getChildren().add(message);
    }

    @FXML
    void startServer() {
        if (ServerLauncher.masterServer == null) {
            ServerLauncher.masterServer = new MasterServer(this);
            ServerLauncher.masterServer.start();
            return;
        }
        if (ServerLauncher.masterServer.getState().equals(Thread.State.NEW)) {
            ServerLauncher.masterServer.start();
            return;
        }
        printToConsole("Server already running", MessageType.WARNING);
    }

    @FXML void exit() {
        Platform.exit();
        System.exit(0);
    }

    @FXML void stopServer() {
        if (ServerLauncher.masterServer == null) {
            printToConsole("No server to stop", MessageType.WARNING);
            return;
        }
        if (ServerLauncher.masterServer.isAlive()) {
            ServerLauncher.masterServer.interrupt();
            /* Dummy client: necessary because of server socket blocked on listening */
            try {
                new Socket("localhost", CommHolder.SERVER_PORT);
            } catch (IOException e) {
                printToConsole(e.getStackTrace().toString(), MessageType.WARNING);
            }
            ServerLauncher.masterServer = null;
        } else {
            printToConsole("No server to stop", MessageType.WARNING);
        }
    }
}
