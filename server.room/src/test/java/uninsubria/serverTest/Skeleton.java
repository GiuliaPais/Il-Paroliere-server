package uninsubria.serverTest;

import uninsubria.server.room.Room;
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
    private int roomId;

    /*-----Constructors-----*/

    public Skeleton(Socket s, RoomList rl) {
        socket = s;
        rooms = rl;
        roomId = 0;
        setInOut();
    }

    /*-----Methods-----*/

    @Override
    public void run() {
        createPlayer();

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
            case "<CREA_STANZA>":
                rooms.createRoom(player);
                break;
            case "<ENTRA_STANZA>":
                entraStanza();
                break;
            case "<ESCI_STANZA>":
                rooms.leaveRoom(roomId, player);
                break;
            default:
                System.out.println(command);

        }
    }

    private void createPlayer() {
       ObjectInputStream input = null;

        try {
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        player = null;

        try {
            player = (Player) input.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void entraStanza() {
        String tmp = "";

        if(rooms.getRoomList().size() == 0)
            tmp = "false";
        else {

            for (int i = 1; i < rooms.getRoomList().size(); i++)
                tmp += rooms.getRoomList().get(i).toSTring();

            out.println(tmp);

            try {
                roomId = Integer.parseInt(in.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }

            rooms.joinRoom(roomId, player);
        }

        out.println(tmp);
    }
}