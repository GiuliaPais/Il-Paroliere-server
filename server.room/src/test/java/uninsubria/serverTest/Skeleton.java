package uninsubria.serverTest;

import uninsubria.server.room.RoomList;

import java.io.*;
import java.net.Socket;
import uninsubria.utils.business.Player;

public class Skeleton implements Runnable{

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private RoomList rooms;
    private Player player;

    /*-----Constructors-----*/

    public Skeleton(Socket s, RoomList rl) {
        socket = s;
        rooms = rl;
        setInOut();
    }

    /*-----Methods-----*/

    @Override
    public void run() {
        while(!socket.isClosed()) {
            try {
                commandList(in.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    /*-----Private methods-----*/

    private void setInOut() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch(IOException ignored) { }
    }

    private void commandList(String command) {
        switch(command) {
            case "<PLAYER>":
                createPlayer();
                break;
            default:
                System.out.println(command);

        }
    }

    private void createPlayer() {
       ObjectInputStream input = null;

        try {
            input = new ObjectInputStream(new FileInputStream("player.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            player = (Player) input.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            input.close();
        } catch (IOException e) {

        }

    }

}