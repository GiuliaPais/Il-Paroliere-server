package uninsubria.serverUDPTest;

import uninsubria.server.room.RoomList;
import uninsubria.utils.business.Player;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
        RoomList roomList = RoomList.getInstance();
        Player player1 = new Player();
        Player player2 = new Player();
        roomList.createRoom(player1);
        roomList.createRoom(player2);

        System.out.println("Creazione RoomList e player avvenuta con successo");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            InetAddress address = InetAddress.getByName("localhost");
            int port = 8888;
            new ClientUDP(address, port).start();
        } catch (UnknownHostException e) {
            System.out.println("Errore col client");
        }

        System.out.println("Tutto è andato bene");

    }

}
