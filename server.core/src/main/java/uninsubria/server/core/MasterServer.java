package uninsubria.server.core;

import javafx.application.Platform;
import uninsubria.server.db.api.ConnectionPool;
import uninsubria.server.db.api.TransactionManager;
import uninsubria.server.dictionary.loader.DictionaryException;
import uninsubria.server.dictionary.manager.DictionaryManager;
import uninsubria.server.email.EmailManager;
import uninsubria.server.gui.MainServerGuiController;
import uninsubria.server.gui.MessageType;
import uninsubria.server.roomlist.RoomList;
import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents the server side logic of the application. Initializes main components and listens on server socket
 * for clients. For each client connected launches a reserved Skeleton thread.
 *
 * @author Giulia Pais
 * @version 0.9.5
 */
public class MasterServer extends Thread {
    /*---Fields---*/
    private MainServerGuiController controllerReference;
    private ServerSocket serverSocket;
    private ConcurrentLinkedQueue<Skeleton> connected_clients;

    /* -- For DB connections -- */
    private String dbHost;
    private String dbName;
    private String dbAdmin;
    private String dbPw;
    private String serverEmail;
    private String serverEmailPassword;

    /*---Constructors---*/
    /**
     * Instantiates a new MasterServer.
     */
    public MasterServer() {
        connected_clients = new ConcurrentLinkedQueue<>();
    }

    /**
     * Instantiates a new MasterServer from an old instance.
     * Useful for recycling credentials when a server is stopped.
     *
     * @param old the old instance
     */
    public MasterServer(MasterServer old) {
        this.controllerReference = old.controllerReference;
        this.serverSocket = null;
        this.dbName = old.dbName;
        this.dbHost = old.dbHost;
        this.dbAdmin = old.dbAdmin;
        this.dbPw = old.dbPw;
        this.serverEmail = old.serverEmail;
        this.serverEmailPassword = old.serverEmailPassword;
        this.connected_clients = new ConcurrentLinkedQueue<>();
    }

    /*---Methods---*/
    @Override
    public void run() {
        try {
            Platform.runLater(() -> controllerReference.printToConsole("Initializing connection pool...", MessageType.MESSAGE));
            ConnectionPool.initializeConnectionPool(dbAdmin, dbPw, dbHost, dbName);
        } catch (SQLException throwables) {
            Platform.runLater(() -> controllerReference.printToConsole(Arrays.toString(throwables.getStackTrace()), MessageType.ERROR));
        }
        EmailManager.initializeEmailManager(serverEmail, serverEmailPassword);
        try {
            Platform.runLater(() -> controllerReference.printToConsole("Loading dictionaries...", MessageType.MESSAGE));
            DictionaryManager.getInstance();
        } catch (IOException | DictionaryException | URISyntaxException e) {
            Platform.runLater(() -> controllerReference.printToConsole(Arrays.deepToString(e.getStackTrace()), MessageType.ERROR));
        }
        /* Needed to reset potential errors if server process was killed abruptly */
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.logoutEveryone();
        Platform.runLater(() -> controllerReference.printToConsole("Initializing room list...", MessageType.MESSAGE));
        RoomList.getInstance();
        try {
            this.serverSocket = new ServerSocket(CommHolder.SERVER_PORT);
            Platform.runLater(() -> controllerReference.printToConsole("Server running, listening on port "+CommHolder.SERVER_PORT, MessageType.MESSAGE));
            while(!isInterrupted()) {
                Platform.runLater(() -> controllerReference.printToConsole("Waiting for connections...", MessageType.MESSAGE));
                Socket client = serverSocket.accept();
                Platform.runLater(() -> controllerReference.printToConsole("Client connected at: "+ client.getInetAddress(), MessageType.MESSAGE));
                client.setKeepAlive(true);
                Skeleton skeleton = new Skeleton(client);
                connected_clients.add(skeleton);
                if (isInterrupted()) {
                    terminate();
                }
            }
        } catch (IOException | SQLException e) {
            Platform.runLater(() -> controllerReference.printToConsole(Arrays.deepToString(e.getStackTrace()), MessageType.ERROR));
        }
    }

    private void terminate() throws IOException, SQLException {
        Platform.runLater(() -> controllerReference.printToConsole("Server shutting down...", MessageType.MESSAGE));
        for (Skeleton s : connected_clients) {
            s.terminate();
            connected_clients.remove(s);
        }
        ConnectionPool.clearPool();
        serverSocket.close();
        Platform.runLater(() -> controllerReference.printToConsole("Server closed", MessageType.MESSAGE));
    }

    /**
     * Removes a Skeleton object from the connected client queue.
     *
     * @param skeleton the skeleton
     */
    public void quitConnectedClient(Skeleton skeleton) {
        connected_clients.remove(skeleton);
    }

    /**
     * Sets db host.
     *
     * @param dbHost the db host
     */
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    /**
     * Sets db name.
     *
     * @param dbName the db name
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * Sets db admin.
     *
     * @param dbAdmin the db admin
     */
    public void setDbAdmin(String dbAdmin) {
        this.dbAdmin = dbAdmin;
    }

    /**
     * Sets db pw.
     *
     * @param dbPw the db pw
     */
    public void setDbPw(String dbPw) {
        this.dbPw = dbPw;
    }

    /**
     * Gets controller reference.
     *
     * @return the controller reference
     */
    public MainServerGuiController getControllerReference() {
        return controllerReference;
    }

    /**
     * Sets controller reference.
     *
     * @param controllerReference the controller reference
     */
    public void setControllerReference(MainServerGuiController controllerReference) {
        this.controllerReference = controllerReference;
    }

    /**
     * Sets server email.
     *
     * @param serverEmail the server email
     */
    public void setServerEmail(String serverEmail) {
        this.serverEmail = serverEmail;
    }

    /**
     * Sets server email password.
     *
     * @param serverEmailPassword the server email password
     */
    public void setServerEmailPassword(String serverEmailPassword) {
        this.serverEmailPassword = serverEmailPassword;
    }
}
