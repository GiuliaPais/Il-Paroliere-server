package uninsubria.clientTest;

public class Main {

    public static void main(String[] args) {

        int[] ip = {151, 48, 158, 26};
        String name = "Ale";

        Client client = new Client(name, ip);
        client.start();
    }
}
