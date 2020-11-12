package uninsubria.server.core;

import javafx.application.Platform;
import uninsubria.server.gui.MainServerGuiController;
import uninsubria.server.gui.MessageType;
import uninsubria.utils.connection.CommHolder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Giulia Pais
 * @version 0.9.0
 */
public class MasterServer extends Thread {
    /*---Fields---*/
    //static references to RoomList and Dictionaries

    private MainServerGuiController controllerReference;
    private ServerSocket serverSocket;

    /*---Constructors---*/
    public MasterServer(MainServerGuiController controller) {
        this.controllerReference = controller;
    }

    /*---Methods---*/
    @Override
    public void run() {
        //Init pool connections for db - ConnectionPoolImpl.initializeConnectionPool
        //Initialize dictionaries
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
        } catch (IOException e) {
            Platform.runLater(() -> controllerReference.printToConsole(e.getStackTrace().toString(), MessageType.ERROR));
        }
    }

    private void terminate() throws IOException {
        Platform.runLater(() -> controllerReference.printToConsole("Server shutting down...", MessageType.MESSAGE));
        serverSocket.close();
        Platform.runLater(() -> controllerReference.printToConsole("Server closed", MessageType.MESSAGE));
    }

}
