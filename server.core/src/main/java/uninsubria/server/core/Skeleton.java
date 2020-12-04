package uninsubria.server.core;

import uninsubria.server.managersimpl.PlayerManager;
import uninsubria.utils.connection.CommProtocolCommands;
import uninsubria.utils.managersAPI.PlayerManagerInterface;
import uninsubria.utils.managersAPI.ProxySkeletonInterface;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * A class representing the Skeleton component in the pattern Proxy-Skeleton.
 * It is a proxy for communication on socket with the client.
 *
 * @author Giulia Pais
 * @version 0.9.2
 */
public class Skeleton extends Thread implements ProxySkeletonInterface {
    /*---Fields---*/
    private Socket client;
    private PlayerManagerInterface playerManager;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /*---Constructors---*/
    public Skeleton(Socket c) throws IOException {
        this.client = c;
        this.client.setKeepAlive(true);
        this.playerManager = new PlayerManager();
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
       } catch (IOException e) {
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
    public void readCommand(String command) throws IOException {
        CommProtocolCommands com = CommProtocolCommands.getByCommand(command);
        switch (com) {
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
                ServiceResultInterface res = playerManager.resendConde(email, requestType);
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
        }
    }

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
        if (playerManager != null) {
            ((PlayerManager) playerManager).disconnect();
            playerManager = null;
        }
        try {
            client.close();
        } catch (IOException ignored) {
        }
    }

}
