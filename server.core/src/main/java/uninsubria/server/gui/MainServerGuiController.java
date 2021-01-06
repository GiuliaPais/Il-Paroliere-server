package uninsubria.server.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uninsubria.server.dbpopulator.DbPopulator;
import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Controller class for the main portion of the server side GUI,
 * mainly composed of a console and a few options to start/stop the server and exit.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class MainServerGuiController {
    /*---Fields---*/
    @FXML StackPane rootContainer;
    @FXML AnchorPane root;
    @FXML TextFlow console;
    @FXML JFXButton back_btn, popDbBtn, cleanDbBtn;

    private final DbPopulator dbPopulator;

    /*---Constructors---*/
    public MainServerGuiController() {
        dbPopulator = new DbPopulator(10);
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
            popDbBtn.setDisable(false);
            cleanDbBtn.setDisable(false);
            return;
        }
        ServerLauncher.newServerInstance();
        ServerLauncher.masterServer.start();
        back_btn.setDisable(true);
        popDbBtn.setDisable(false);
        cleanDbBtn.setDisable(false);
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
        popDbBtn.setDisable(true);
        cleanDbBtn.setDisable(true);
    }

    @FXML void back() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DbLogin.fxml"));
        Parent parent = loader.load();
        root.getScene().setRoot(parent);
    }

    @FXML void populate(){
        try {
            dbPopulator.populate();
            printToConsole("Database populated", MessageType.MESSAGE);
        } catch (NoSuchAlgorithmException | SQLException | InterruptedException e) {
            printToConsole(e.getMessage(), MessageType.ERROR);
        }
    }

    @FXML void clean(){
        JFXDialog dialog = new JFXDialog();
        JFXDialogLayout content = new JFXDialogLayout();
        Label bodyContent = new Label("Are you sure you want to clear the database? All data will be lost and " +
                "it won't be possible to retrieve it");
        content.setBody(bodyContent);
        JFXButton yes = new JFXButton("YES");
        JFXButton no = new JFXButton("NO");
        no.setOnAction(event -> dialog.close());
        yes.setOnAction(event -> {
            try {
                dbPopulator.clearAll();
                printToConsole("Database cleaned", MessageType.MESSAGE);
            } catch (SQLException | InterruptedException throwables) {
                printToConsole(throwables.getMessage(), MessageType.ERROR);
            }
            dialog.close();
        });
        content.setActions(yes, no);
        dialog.setDialogContainer(rootContainer);
        dialog.setContent(content);
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.show();
    }
}
