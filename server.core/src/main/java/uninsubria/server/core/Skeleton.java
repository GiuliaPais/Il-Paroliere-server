package uninsubria.server.core;

import uninsubria.server.managersimpl.PlayerManager;
import uninsubria.utils.connection.CommProtocolCommands;
import uninsubria.utils.managersAPI.PlayerManagerInterface;
import uninsubria.utils.managersAPI.ProxySkeletonInterface;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.*;
import java.net.Socket;

/**
 * A class representing the Skeleton component in the pattern Proxy-Skeleton.
 * It is a proxy for communication on socket with the client.
 *
 * @author Giulia Pais
 * @version 0.9.1
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
        this.in = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
        start();
    }

    /*---Methods---*/
    @Override
    public void run() {
       try {
           String command = in.readUTF();
           while(!command.equals(CommProtocolCommands.QUIT.getCommand())) {
               readCommand(command);
               command = in.readUTF();
           }
           terminate();
       } catch (IOException e) {
           e.printStackTrace();
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
        }
    }

    private void terminate() throws IOException {
        in.close();
        out.close();
        playerManager = null;
        client.close();
    }
}
