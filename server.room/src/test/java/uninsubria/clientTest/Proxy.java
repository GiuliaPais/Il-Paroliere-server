package uninsubria.clientTest;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Proxy implements ClientInterface {

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private boolean active;

    /*-----Constructors-----*/

    public Proxy(String host, int port) {
        try {
            InetAddress addr = InetAddress.getByName(host);
            init(addr, port);
        } catch(IOException ignored) { }
    }

    public Proxy(byte[] ip, int port) {
        try {
            InetAddress addr = InetAddress.getByAddress(ip);
            init(addr, port);
        } catch (IOException ignored) { }
    }

    public Proxy(int[] ip, int port) {
        try {
            InetAddress addr = InetAddress.getByAddress(fromIntToByte(ip));
            init(addr, port);
        } catch (IOException ignored) { }
    }

    public Proxy(InetAddress addr, int port) {
        try {
            init(addr, port);
        } catch (IOException ignored) { }
    }

    public Proxy() {
        try {
            InetAddress addr = InetAddress.getByName("localhost");
            init(addr, ClientInterface.PORT);
        } catch (IOException ignored) { }
    }

    /*-----Methods-----*/

    @Override
    public String readIn() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void printOut(String s) {
        out.println(s);
    }

    @Override
    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException ignored) { }

        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public Socket getSocket() {
        return socket;
    }

    /*-----Private methods-----*/

    private void init(InetAddress addr, int port) throws IOException {
        active = true;
        socket = new Socket(addr, port);
        setInOut();
    }

    private void setInOut() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    private byte[] fromIntToByte(int[] array) {
        byte[] ip = new byte[array.length];

        for(int i = 0; i < ip.length; i++)
            ip[i] = (byte)array[i];

        return ip;
    }
}