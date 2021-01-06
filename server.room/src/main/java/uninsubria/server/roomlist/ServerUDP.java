package uninsubria.server.roomlist;

import uninsubria.utils.connection.CommHolder;
import uninsubria.utils.connection.CommProtocolCommands;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Thread that actively listens for periodic requests on a
 * datagram socket for client updates.
 *
 * @author Davide Di Giovanni
 * @author Giulia Pais (minor)
 * @version 0.9.4
 */
class ServerUDP extends Thread {

    /*---Fields---*/
    // Ricezione
    private DatagramSocket datagramSocket;
    private InetAddress address;
    private int clientPort;
    private boolean running;

    /*---Constructor---*/
    /**
     * Instantiates a new Server udp.
     */
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
        if (string.contains("|")) {
            List<String> splitted = Pattern.compile("\\|")
                    .splitAsStream(string)
                    .collect(Collectors.toList());
            CommProtocolCommands command = CommProtocolCommands.getByCommand(splitted.get(0));
            switch (command) {
                case SEND_PLIST -> {
                    UUID roomID = UUID.fromString(splitted.get(1));
                    if (RoomList.getRoom(roomID) != null) {
                        sendObject(RoomList.getRoom(roomID).getCurrentPlayers());
                    }
                }
                default -> {}
            }
            return;
        }
        CommProtocolCommands command = CommProtocolCommands.getByCommand(string);
        switch(command) {
            case SEND_ROOM_LIST -> sendRoomList();
            default -> {}
        }
    }

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