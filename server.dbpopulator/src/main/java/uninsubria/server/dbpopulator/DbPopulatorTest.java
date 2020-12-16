package uninsubria.server.dbpopulator;

import uninsubria.server.db.api.ConnectionPool;
import uninsubria.server.dictionary.loader.DictionaryException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import static uninsubria.server.db.api.ConnectionPool.initializeConnectionPool;

class DbPopulatorTest {

    public static void main(String[] args) throws IOException, DictionaryException, URISyntaxException, InterruptedException, NoSuchAlgorithmException, SQLException {
        int playerEntries = 10;
        int gameInfoEntries = 1;
        ConnectionPool.initializeConnectionPool("postgres","qwerty","localhost","Il Paroliere");
        System.out.println("1 Connessione effettuata");
        DbPopulator dbP = new DbPopulator(playerEntries,gameInfoEntries);
        System.out.println("2 predisposizione dello script eseguito");
        dbP.clearAll();
        System.out.println("3 Database svuotato");
        dbP.populate();
        System.out.println("4 Script eseguito con successo");

    }
}

