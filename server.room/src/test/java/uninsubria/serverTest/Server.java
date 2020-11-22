package uninsubria.serverTest;

import uninsubria.server.room.RoomList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(ServerInterface.PORT);
        RoomList rooms = RoomList.getInstance();
        System.out.println("Server attivo");

        while(true) {
            System.out.println("In attesa di connessioni");
            Socket socket = serverSocket.accept();
            new Thread(new Skeleton(socket, rooms)).start();
        }
    }

}
