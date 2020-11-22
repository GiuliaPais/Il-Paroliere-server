package uninsubria.clientTest;

public class Main {

    public static void main(String[] args) {

        int[] ip = {151, 48, 158, 26};
        String name = "Dave";

        Client client = new Client(name);
        client.start();
    }
}
