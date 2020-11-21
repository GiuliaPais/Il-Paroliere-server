package uninsubria.server.core;

import javafx.application.Platform;
import uninsubria.server.db.api.ConnectionPool;
import uninsubria.server.dictionary.loader.DictionaryException;
import uninsubria.server.dictionary.manager.DictionaryManager;
import uninsubria.server.gui.MainServerGuiController;
import uninsubria.server.gui.MessageType;
import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.sql.SQLException;

/**
 * @author Giulia Pais
 * @version 0.9.1
 */
public class MasterServer extends Thread {
    /*---Fields---*/
    private MainServerGuiController controllerReference;
    private ServerSocket serverSocket;

    /* -- For DB connections -- */
    private String dbHost;
    private String dbName;
    private String dbAdmin;
    private String dbPw;
    private String serverEmail;
    private String serverEmailPassword;

    /*---Constructors---*/
    public MasterServer() {
    }

    public MasterServer(MasterServer old) {
        this.controllerReference = old.controllerReference;
        this.dbName = old.dbName;
        this.dbHost = old.dbHost;
        this.dbAdmin = old.dbAdmin;
        this.dbPw = old.dbPw;
        this.serverEmail = old.serverEmail;
        this.serverEmailPassword = old.serverEmailPassword;
    }

    /*---Methods---*/
    @Override
    public void run() {
        try {
            Platform.runLater(() -> controllerReference.printToConsole("Initializing connection pool...", MessageType.MESSAGE));
            ConnectionPool.initializeConnectionPool(dbAdmin, dbPw, dbHost, dbName);
        } catch (SQLException throwables) {
            Platform.runLater(() -> controllerReference.printToConsole(throwables.getStackTrace().toString(), MessageType.ERROR));
        }
        try {
            Platform.runLater(() -> controllerReference.printToConsole("Loading dictionaries...", MessageType.MESSAGE));
            DictionaryManager.getInstance();
        } catch (IOException e) {
            Platform.runLater(() -> controllerReference.printToConsole(e.getStackTrace().toString(), MessageType.ERROR));
        } catch (DictionaryException e) {
            Platform.runLater(() -> controllerReference.printToConsole(e.getStackTrace().toString(), MessageType.ERROR));
        } catch (URISyntaxException e) {
            Platform.runLater(() -> controllerReference.printToConsole(e.getStackTrace().toString(), MessageType.ERROR));
        }
        //Initialize room list
        try {
            this.serverSocket = new ServerSocket(CommHolder.SERVER_PORT);
            Platform.runLater(() -> controllerReference.printToConsole("Server running, listening on port "+CommHolder.SERVER_PORT, MessageType.MESSAGE));
            while(!isInterrupted()) {
                Platform.runLater(() -> controllerReference.printToConsole("Waiting for connections...", MessageType.MESSAGE));
                Socket client = serverSocket.accept();
                Platform.runLater(() -> controllerReference.printToConsole("Client connected at: "+ client.getInetAddress(), MessageType.MESSAGE));
                client.setKeepAlive(true);
                new Skeleton(client);
                if (isInterrupted()) {
                    terminate();
                }
            }
        } catch (IOException | SQLException e) {
            Platform.runLater(() -> controllerReference.printToConsole(e.getStackTrace().toString(), MessageType.ERROR));
        }
    }

    private void terminate() throws IOException, SQLException {
        Platform.runLater(() -> controllerReference.printToConsole("Server shutting down...", MessageType.MESSAGE));
        ConnectionPool.clearPool();
        serverSocket.close();
        Platform.runLater(() -> controllerReference.printToConsole("Server closed", MessageType.MESSAGE));
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setDbAdmin(String dbAdmin) {
        this.dbAdmin = dbAdmin;
    }

    public void setDbPw(String dbPw) {
        this.dbPw = dbPw;
    }

    public MainServerGuiController getControllerReference() {
        return controllerReference;
    }

    public void setControllerReference(MainServerGuiController controllerReference) {
        this.controllerReference = controllerReference;
    }

    public void setServerEmail(String serverEmail) {
        this.serverEmail = serverEmail;
    }

    public void setServerEmailPassword(String serverEmailPassword) {
        this.serverEmailPassword = serverEmailPassword;
    }
}
