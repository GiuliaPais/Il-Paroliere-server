package uninsubria.server.core;

import uninsubria.server.gui.ServerLauncher;
import uninsubria.server.playerManagerImpl.PlayerManager;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.business.Player;
import uninsubria.utils.connection.CommProtocolCommands;
import uninsubria.utils.managersAPI.PlayerManagerInterface;
import uninsubria.utils.managersAPI.ProxySkeletonInterface;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

/**
 * A class representing the Skeleton component in the pattern Proxy-Skeleton.
 * It is a proxy for communication on socket with the client.
 *
 * @author Giulia Pais
 * @version 0.9.7
 */
public class Skeleton extends Thread implements ProxySkeletonInterface {
    /*---Fields---*/
    private final Socket client;
    private PlayerManagerInterface playerManager;
    private final ObjectOutputStream out;
    private ObjectInputStream in;

    /*---Constructors---*/
    /**
     * Instantiates a new Skeleton.
     *
     * @param c The client socket
     * @throws IOException if there are problems opening streams
     */
    public Skeleton(Socket c) throws IOException {
        this.client = c;
        this.client.setKeepAlive(true);
        this.playerManager = new PlayerManager(client.getInetAddress());
        this.out = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
        start();
    }

    /*---Methods---*/
    @Override
    public void run() {
       try {
           this.in = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
           String command = in.readUTF();
           while(!command.equals(CommProtocolCommands.QUIT.getCommand())) {
               readCommand(command);
               command = in.readUTF();
           }
           terminate();
       } catch (IOException | ClassNotFoundException e) {
           terminate();
       }
    }

    @Override
    public void writeCommand(CommProtocolCommands command, Object... params) throws IOException {
        out.writeUTF(command.getCommand());
        for (Object p : params) {
            if (p instanceof String) {
                String s = (String) p;
                out.writeUTF(s);
            } else {
                out.writeObject(p);
            }
        }
        out.flush();
    }


    @Override
    public void readCommand(String command) throws IOException, ClassNotFoundException {
        CommProtocolCommands com = CommProtocolCommands.getByCommand(command);
        switch (Objects.requireNonNull(com)) {
            case ACTIVATION_CODE -> {
                String name = in.readUTF();
                String lastname = in.readUTF();
                String userid = in.readUTF();
                String email = in.readUTF();
                String password = in.readUTF();
                ServiceResultInterface res = playerManager.requestActivationCode(name, lastname, userid, email, password);
                writeCommand(CommProtocolCommands.ACTIVATION_CODE, res);
            }
            case CONFIRM_ACTIVATION_CODE -> {
                String email = in.readUTF();
                String code = in.readUTF();
                ServiceResultInterface res = playerManager.confirmActivationCode(email, code);
                writeCommand(CommProtocolCommands.CONFIRM_ACTIVATION_CODE, res);
            }
            case RESEND_CODE -> {
                String email = in.readUTF();
                String requestType = in.readUTF();
                ServiceResultInterface res = playerManager.resendCode(email, requestType);
                writeCommand(CommProtocolCommands.RESEND_CODE, res);
            }
            case LOGIN -> {
                String identifier = in.readUTF();
                String pw = in.readUTF();
                ServiceResultInterface res = playerManager.login(identifier, pw);
                writeCommand(CommProtocolCommands.LOGIN, res);
            }
            case LOGOUT -> {
                String id = in.readUTF();
                playerManager.logout(id);
            }
            case UPDATE_PLAYER_INFO -> {
                Player player = (Player) in.readObject();
                playerManager.updatePlayerInfo(player);
            }
            case CHANGE_USER_ID -> {
                String oldID = in.readUTF();
                String newID = in.readUTF();
                ServiceResultInterface res = playerManager.changeUserId(oldID, newID);
                writeCommand(CommProtocolCommands.CHANGE_USER_ID, res);
            }
            case CHANGE_PW -> {
                String email = in.readUTF();
                String oldPw = in.readUTF();
                String newPw = in.readUTF();
                ServiceResultInterface res = playerManager.changePassword(email, oldPw, newPw);
                writeCommand(CommProtocolCommands.CHANGE_PW, res);
            }
            case RESET_PW -> {
                String email = in.readUTF();
                ServiceResultInterface res = playerManager.resetPassword(email);
                writeCommand(CommProtocolCommands.RESET_PW, res);
            }
            case DELETE_PROFILE -> {
                String id = in.readUTF();
                String pw = in.readUTF();
                ServiceResultInterface res = playerManager.deleteProfile(id, pw);
                writeCommand(CommProtocolCommands.DELETE_PROFILE, res);
            }
            case CREATE_ROOM -> {
                Lobby lobby = (Lobby) in.readObject();
                playerManager.createRoom(lobby);
                writeCommand(CommProtocolCommands.CREATE_ROOM);
            }
            case LEAVE_ROOM -> {
                UUID roomID = (UUID) in.readObject();
                playerManager.leaveRoom(roomID);
            }
            case JOIN_ROOM -> {
                UUID roomID = (UUID) in.readObject();
                ServiceResultInterface res = playerManager.joinRoom(roomID);
                writeCommand(CommProtocolCommands.JOIN_ROOM, res);
            }
            case FETCH_STATS -> {
                ServiceResultInterface res = playerManager.fetchStatistics();
                writeCommand(CommProtocolCommands.FETCH_STATS, res);
            }
            case WORD_STATS -> {
                String word = in.readUTF();
                ServiceResultInterface res = playerManager.requestWordStats(word);
                writeCommand(CommProtocolCommands.WORD_STATS, res);
            }
            case LEAVE_GAME -> {
                UUID roomID = (UUID) in.readObject();
                playerManager.leaveGame(roomID);
            }
        }
    }

    /**
     * Correctly terminates the thread by trying to close open resources
     * and if a player is logged it logs out.
     */
    public void terminate() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
        try {
            out.close();
        } catch (IOException ignored) {
        }
        try {
            if (playerManager != null) {
                playerManager.quit();
                playerManager = null;
            }
            client.close();
        } catch (IOException ignored) {
        }
        ServerLauncher.masterServer.quitConnectedClient(this);
    }

}
