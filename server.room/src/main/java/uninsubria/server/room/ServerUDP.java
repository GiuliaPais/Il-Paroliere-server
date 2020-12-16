package uninsubria.server.room;

import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.connection.CommHolder;
import uninsubria.utils.connection.CommProtocolCommands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.UUID;

public class ServerUDP extends Thread {

    /*---Fields---*/
    // Ricezione
    private DatagramSocket datagramSocket;
    private InetAddress address;
    private boolean running;

    // Invio
    private ByteArrayOutputStream byteArrayOutputStream;
    private ObjectOutputStream objectOutputStream;

    /*---Constructor---*/
    public ServerUDP() {
        this.initialize();
        this.start();
    }

    /*---Methods---*/
    public void run() {
        running = true;

        while (running) {
            String string = this.receiveString();
            this.waitCommand(string);
        }
    }

    public void interrupt() {
        super.interrupt();
        running = false;
        this.close();
    }

    public void sendPlayersList(UUID roomId) {
        ArrayList<PlayerWrapper> playersList = RoomList.getRoom(roomId).getPlayerSlots();

        for(int i = 0; i < playersList.size(); i++) {
            PlayerWrapper pw = playersList.get(i);
            address = pw.getIpAddress();
            this.sendObject(pw.getPlayer().getName());
        }
    }

    /*---Private methods---*/
    private void initialize() {
        try {
            datagramSocket = new DatagramSocket(CommHolder.SERVER_PORT);
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Riceve una semplice stringa e la restituisce come risultato
    private String receiveString() {
        try {
            byte[] stringBytes = new byte[256];
            DatagramPacket packet = new DatagramPacket(stringBytes, stringBytes.length);
            datagramSocket.receive(packet);
            address = datagramSocket.getInetAddress();

            return new String(packet.getData(), 0, packet.getLength());

        } catch (IOException e) {
            return "Missing String";
        }
    }

    // Permette di mandare la lista delle attuali stanze
    private void sendRoomList() {
        ArrayList<Lobby> rooms = RoomList.getRoomsAsLobbies();
        this.sendObject(rooms);
    }



    // Attende la stringa col comando corrispondente. Se non è quella che si aspetta, non fa nulla.
    private void waitCommand(String string) {
        CommProtocolCommands command = CommProtocolCommands.getByCommand(string);

        switch(command) {
            case SEND_ROOM_LIST:
                this.sendRoomList();
                break;
            default:
                break;
        }
    }

    private void close() {
        try {
            objectOutputStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        datagramSocket.close();
    }

    private void sendObject(Serializable object) {
        try {
            objectOutputStream.writeObject(object);
            byte[] objectBytes = byteArrayOutputStream.toByteArray();
            DatagramPacket datagramPacket = new DatagramPacket(objectBytes, objectBytes.length, address, CommHolder.SERVER_PORT);
            datagramSocket.send(datagramPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}