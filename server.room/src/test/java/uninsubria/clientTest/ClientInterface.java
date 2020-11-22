package uninsubria.clientTest;

public interface ClientInterface {

    public static final int PORT = 8888;

    String readIn();
    void printOut(String s);
    void close();
}
