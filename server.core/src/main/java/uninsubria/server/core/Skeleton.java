package uninsubria.server.core;

import uninsubria.server.managersimpl.PlayerManager;
import uninsubria.utils.managersAPI.PlayerManagerInterface;

import java.net.Socket;

/**
 * @author Giulia Pais
 * @version 0.9.0
 */
public class Skeleton extends Thread {
    /*---Fields---*/
    private Socket client;
    private PlayerManagerInterface playerManager;

    /*---Constructors---*/
    public Skeleton(Socket c) {
        this.client = c;
        this.playerManager = new PlayerManager();
        start();
    }

    /*---Methods---*/

    @Override
    public void run() {
        //try with resources? Keep listening on socket for I/O
    }
}
