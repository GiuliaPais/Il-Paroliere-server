package uninsubria.serverUDPTest;

import javafx.beans.property.MapProperty;
import uninsubria.server.room.Room;
import uninsubria.utils.connection.CommProtocolCommands;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientUDP extends Thread {

    private DatagramSocket datagramSocket;
    private InetAddress address;
    private int port;
    private boolean running;

    public ClientUDP(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.initialize();
    }

    public void run() {
        running = true;

        while(running) {
            this.requestList();
            MapProperty<Integer, Room> rooms = this.getList();

            System.out.println(rooms.asString());

            this.interrupt();
        }
    }

    public void requestList() {
        byte[] stringBytes = CommProtocolCommands.SEND_LIST.getCommand().getBytes();
        DatagramPacket packet = new DatagramPacket(stringBytes, stringBytes.length, address, port);

        try {
            datagramSocket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MapProperty<Integer, Room> getList() {
        try {
            byte[] incomingObject = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(incomingObject, incomingObject.length);
            datagramSocket.receive(datagramPacket);

            byte[] data = datagramPacket.getData();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (MapProperty<Integer, Room>) objectInputStream.readObject();

        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initialize() {
        try {
            datagramSocket = new DatagramSocket();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
