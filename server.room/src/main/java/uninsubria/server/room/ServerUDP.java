package uninsubria.server.room;

import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.connection.CommHolder;
import uninsubria.utils.connection.CommProtocolCommands;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Thread that actively listens for periodic requests on a
 * datagram socket for client updates.
 *
 * @author Davide Di Giovanni
 * @author Giulia Pais (minor)
 * @version 0.9.2
 */
public class ServerUDP extends Thread {

    /*---Fields---*/
    // Ricezione
    private DatagramSocket datagramSocket;
    private InetAddress address;
    private int clientPort;
    private boolean running;

    /**
     * Instantiates a new Server udp.
     */
    /*---Constructor---*/
    public ServerUDP() {
        this.initialize();
        this.start();
    }

    /*---Methods---*/
    public void run() {
        running = true;

        while (running) {
            String string = receiveString();
            waitCommand(string);
        }
    }

    public void interrupt() {
        super.interrupt();
        running = false;
//        this.close();
    }

    /**
     * Send players list.
     *
     * @param roomId the room id
     */
    public void sendPlayersList(UUID roomId) {
        if (RoomList.getRoom(roomId) == null) {
            return;
        }
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
            address = packet.getAddress();
            clientPort = packet.getPort();

            return new String(packet.getData(), 0, packet.getLength());

        } catch (IOException e) {
            return "Missing String";
        }
    }

    // Permette di mandare la lista delle attuali stanze
    private void sendRoomList() {
        sendObject(RoomList.getLobbies());
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

//    private void close() {
//        try {
//            objectOutputStream.close();
//            byteArrayOutputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        datagramSocket.close();
//    }

    private void sendObject(Serializable object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteArrayOutputStream));
            objectOutputStream.flush();
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            byte[] objectBytes = byteArrayOutputStream.toByteArray();
            DatagramPacket datagramPacket = new DatagramPacket(objectBytes, objectBytes.length, address, clientPort);
            datagramSocket.send(datagramPacket);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}