package uninsubria.clientTest;

import uninsubria.utils.business.Player;

import java.io.*;

public class Client extends Thread {

    private BufferedReader in;
    private Proxy client;
    private Player player;

    /*-----Constructors-----*/

    public Client() {
        client = new Proxy();
        in = new BufferedReader(new InputStreamReader(System.in));
        setPlayer("Default");
    }

    public Client(String name) {
        client = new Proxy();
        in = new BufferedReader(new InputStreamReader(System.in));
        setPlayer(name);
    }

    public Client(String name, int[] ip) {
        client = new Proxy(ip, ClientInterface.PORT);
        in = new BufferedReader(new InputStreamReader(System.in));
        setPlayer(name);
    }

    /*-----Methods-----*/

    public void run() {
        while(client.isActive()) {
            sendPlayer();
            selectCommand();

        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        client.close();
    }

    public Player getPlayer() {
        return player;
    }

    /*-----Private methods-----*/

    private void setPlayer(String name) {
        player = new Player("Default", "Default", name,
                "Default", "Default", 0, true);
    }

    private void selectCommand() {
        System.out.println("Cosa vuoi fare?");
        System.out.println("1) Crea una stanza.");
        System.out.println("2) Entra in una stanza.");
        System.out.println("3) Esci dalla stanza.");
        System.out.println("4) Disconnettiti.");
        System.out.print("Scegli digitando il numero corrispondente: -> ");
        int i = 0;

        try {
            i = Integer.parseInt(in.readLine());
        } catch (Exception e) {
            System.out.println("Comando non riconosciuto. Digitare di nuovo.");
            selectCommand();
        }

        commandList(i);
    }

    private void commandList(int i) {
        switch(i) {
            case 1:
                client.printOut("<CREA_STANZA>");
                break;
            case 2:
                client.printOut("<ENTRA_STANZA>");
                System.out.print("Digitare il numero della stanza: -> ");
                int tmp = 0;

                try {
                    tmp = Integer.parseInt(in.readLine());
                } catch (Exception e) {
                    System.out.println("Comando non riconosciuto. Digitare di nuovo.");
                    selectCommand();
                }

                break;
            case 3:
                client.printOut("<ESCI_STANZA>");
                break;
            case 4:
                interrupt();
                break;
            default:
                System.out.println("Comando non riconosciuto. Digitare di nuovo.");
        }
    }

    private void sendPlayer() {
        client.printOut("<PLAYER>");

        ObjectOutputStream output = null;

        try {
            output = new ObjectOutputStream(new FileOutputStream("player.dat"));
            output.writeObject(player);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
