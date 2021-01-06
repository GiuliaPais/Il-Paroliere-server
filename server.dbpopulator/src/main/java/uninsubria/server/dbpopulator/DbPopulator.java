package uninsubria.server.dbpopulator;

import uninsubria.server.db.api.ConnectionPool;
import uninsubria.server.db.businesslayer.GameEntry;
import uninsubria.server.db.businesslayer.GameInfo;
import uninsubria.server.db.dao.*;
import uninsubria.utils.business.Player;
import uninsubria.utils.languages.Language;
import uninsubria.utils.security.PasswordEncryptor;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Utility object for generating sample data for database population.
 *
 * @author Alessandro Lerro
 */
public class DbPopulator {
    /*---Fields---*/
    private final PlayerDAO playerDAO;
    private final GameEntryDAO gameEntryDAO;
    private final GameInfoDAO gameInfoDAO;
    private final int playerEntries;
    private final List<Player> generatedPlayers;
    private final List<GameEntry> generatedEntries;
    private final List<GameInfo> generatedGames;
    private final Random random = new Random();
    private final String[] possibleNames = new String[] {"Andrea", "Marco", "Lorenzo", "Leonardo", "Alessandro",
    "Davide", "Mattia", "Gabriele", "Luca", "Edoardo", "Riccardo", "Pietro", "Tommaso", "Matteo", "Francesco",
    "Sofia", "Giulia", "Alice", "Aurora", "Emma", "Giorgia", "Chiara", "Anna", "Beatrice", "Lucia"};
    private final String[] possibleLastNames = new String[] {"Rossi", "Bianchi", "Ferrari", "Russo", "Gallo",
    "Costa", "Fontana", "Conti", "Ricci", "Rinaldi", "Longo", "Gatti", "Galli", "Ferraro", "Santoro", "Ferri",
    "Colombo", "De Luca", "Barbieri", "Sala", "Villa", "Cattaneo", "Brambilla", "Fumagalli", "Trevisan", "Magnani"};
    private final List<String> moves = new ArrayList<>(Arrays.asList("UP", "DOWN", "LEFT", "RIGHT", "RIGHTUP", "RIGHTDOWN", "LEFTUP", "LEFTDOWN"));

    /*---Constructors---*/
    /**
     * Instantiates a new Db populator.
     *
     * @param playerEntries the player entries to generate
     */
    public DbPopulator(int playerEntries) {
        this.playerDAO = new PlayerDAOImpl();
        this.gameEntryDAO = new GameEntryDAOImpl();
        this.gameInfoDAO = new GameInfoDAOImpl();
        this.playerEntries = playerEntries;
        this.generatedPlayers = new ArrayList<>();
        this.generatedGames = new ArrayList<>();
        this.generatedEntries = new ArrayList<>();
    }

    /*---Methods---*/
    private String generateRandomAlphaNumString(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Clears all tuples from database.
     *
     * @throws SQLException         the sql exception
     * @throws InterruptedException the interrupted exception
     */
    public void clearAll() throws SQLException, InterruptedException {
        Connection connection = ConnectionPool.getConnection();
        String clearGameEntry = "DELETE FROM GAMEENTRY";
        String clearPlayer = "DELETE FROM PLAYER";
        String clearGameInfo = "DELETE FROM GAMEINFO";
        PreparedStatement statement = connection.prepareStatement(clearGameEntry);
        statement.executeUpdate();
        statement = connection.prepareStatement(clearPlayer);
        statement.executeUpdate();
        statement = connection.prepareStatement(clearGameInfo);
        statement.executeUpdate();
        ConnectionPool.releaseConnection(connection);
    }

    /**
     * Populates the database.
     *
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws SQLException             the sql exception
     * @throws InterruptedException     the interrupted exception
     */
    public void populate() throws NoSuchAlgorithmException, SQLException, InterruptedException {
        createDummyPlayers();
        createDummyGames();
        populatePlayerTable();
        populateGameInfoTable();
        populateGameEntryTable();
    }

    private void populatePlayerTable() throws SQLException, InterruptedException {
        playerDAO.setConnection(ConnectionPool.getConnection());
        for (Player p : generatedPlayers) {
            playerDAO.create(p);
        }
        ConnectionPool.releaseConnection(playerDAO.getConnection());
    }

    private void populateGameInfoTable() throws SQLException, InterruptedException {
        gameInfoDAO.setConnection(ConnectionPool.getConnection());
        for (GameInfo gameInfo : generatedGames) {
            gameInfoDAO.create(gameInfo);
        }
        ConnectionPool.releaseConnection(gameInfoDAO.getConnection());
    }

    private void populateGameEntryTable() throws SQLException, InterruptedException {
        gameEntryDAO.setConnection(ConnectionPool.getConnection());
        for (GameEntry gameEntry : generatedEntries) {
            gameEntryDAO.create(gameEntry);
        }
        ConnectionPool.releaseConnection(gameEntryDAO.getConnection());
    }

    private void createDummyPlayers() throws NoSuchAlgorithmException {
        for (int i = 0; i < playerEntries; i++) {
            Player player = new Player();
            player.setPlayerID("user" + (i+1));
            player.setEmail(player.getPlayerID() + "@example.com");
            player.setSurname(possibleLastNames[random.nextInt(possibleLastNames.length)]);
            player.setName(possibleNames[random.nextInt(possibleNames.length)]);
            player.setLogStatus(random.nextBoolean());
            player.setProfileImage(random.nextInt(10)+1);
            player.setPassword(PasswordEncryptor.hashPassword(generateRandomAlphaNumString(10)));
            generatedPlayers.add(player);
        }
    }

    private void createDummyGames() {
        for (int i = 1; i <= 7/*gameInfoEntries*/; i++) {
            GameInfo gameInfo = new GameInfo();
            gameInfo.setGameId(UUID.randomUUID());
            gameInfo.setRuleset("STANDARD");
            gameInfo.setLanguage(Language.ITALIAN);
            int num_players = switch(i){
                case(1), (2) -> 2;
                case(3), (4) -> 3;
                case(5) -> 4;
                case(6) -> 5;
                case(7) -> 6;
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };
            gameInfo.setNumPlayers(num_players);
            String[] grid = createDummyGameEntries(gameInfo.getNumPlayers(), gameInfo.getGameId(), i);
            gameInfo.setAllMatchesGrid(grid);
            generatedGames.add(gameInfo);
        }
    }

    private String[] createDummyGameEntries2_1(List<Player> players, UUID gameID, List<String> grids) {
        Random rdm = new Random();
        //MATCH 1
        for (String x: WordsOfGrid.GRID0.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "FINTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "LISTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "PISTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "TINTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "PETI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "LIMITI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "FINE", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "STIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "SLIP", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "INTIMI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "PENTITI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "FINE", rdm.nextBoolean(),true,false));
        //MATCH 2
        for (String x: WordsOfGrid.GRID11.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "BEVUTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "RUDERE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "BARBA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "AVERE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "EREDE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "ETERE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "AVREBBE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "UVA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "ETERA", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "BERE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "ERBA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "BREVE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "AVRETE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "VEDE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "VERE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "UNTA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "ETERA", rdm.nextBoolean(),true,false));
        //MATCH 3
        for (String x: WordsOfGrid.GRID10.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "POETA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "NOVA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "VETO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "ATEO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "RITO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "NATO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "VANO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "RIPA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "EVO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "AVO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "IRTO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "TIR", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "POTEVANO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "NOVE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "VOTI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "TOPI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "VOTO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "NOTO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 3, "NOTA", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 3, "VOTO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 3, "NOTO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 3, "NOTA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 3, "POTAVO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 3, "POTEVANO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 3, "POTEVO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 3, "VATE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 3, "TIPO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 3, "TOT", rdm.nextBoolean(),false,false));
        //MATCH 4
        for (String x: WordsOfGrid.GRID6.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 4, "REGIME", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 4, "FINE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 4, "RENI", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 4, "TIR", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 4, "NERI", rdm.nextBoolean(),false,true));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 4, "GENI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 4, "MIE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 4, "NEI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 4, "TIR", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 4, "NERI", rdm.nextBoolean(),false,true));
        //MATCH 5
        for (String x: WordsOfGrid.GRID1.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 5, "DELEGA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 5, "DAGA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 5, "MAGO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 5, "MAGA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 5, "LEGA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 5, "DAMA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 5, "MODA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 5, "AEDO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 5, "GEL", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 5, "LEGAMI", rdm.nextBoolean(),false,true));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 5, "AGO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 5, "ODE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 5, "OMEGA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 5, "EGO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 5, "AMAI", rdm.nextBoolean(),false,true));
        //return statement
        return grids.toArray(new String[grids.size()]);
    }

    private String[] createDummyGameEntries2_2(List<Player> players, UUID gameID, List<String> grids) {
        Random rdm = new Random();
        //MATCH 1
        for (String x: WordsOfGrid.GRID2.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "MANSARDA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "MANDARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "MANDATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "DURATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "STRADA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "ANDARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "DATARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "RASATE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "ANDATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "MADRE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "ANATRA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "SARDA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "STARE", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "MANE", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "MANATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "AMANTE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "RADA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "DAMA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "ERTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "ARTE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "TANA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "ANSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "IDATA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "TRE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "STARE", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "MANE", rdm.nextBoolean(),true,false));
        //MATCH 2
        for (String x: WordsOfGrid.GRID2.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "MANSARDA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "MANDARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "MANDATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "DURATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "STRADA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "ANDARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "DATARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "RASATE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "ANDATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "MADRE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "ALPI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "PUOI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "PALO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "CLAN", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "BOIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "LANA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "PIO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "PAIO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "PAIA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "POI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "CON", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "ANA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "PALO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "CLAN", rdm.nextBoolean(),true,false));
        //return statement
        return grids.toArray(new String[grids.size()]);
    }

    private String[] createDummyGameEntries3_1(List<Player> players, UUID gameID, List<String> grids) {
        Random rdm = new Random();
        //MATCH 1
        for (String x: WordsOfGrid.GRID4.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "TARSIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "SCIARADA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "NASCITA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "CALARSI", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "LANCIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "DASTRICA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "MALATI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "CIALDA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 1, "FASCIA", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "CIARLA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "FALDA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "TASCA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "FLAN", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "TIARA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "BAIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "CASA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "FALSATI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "SALATI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 1, "CIALDA", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 1, "ASCIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 1, "RADA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 1, "BAITA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 1, "BASTIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 1, "FAN", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 1, "SLANCIA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 1, "BUFALA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 1, "FASCIA", rdm.nextBoolean(),true,false));
        //MATCH 2
        for (String x: WordsOfGrid.GRID5.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "CHIOSARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "REATO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "OSARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "COSTARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "STARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "COSTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "CORSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "SANTO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "CORANI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "DORATI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "OSTARE", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), (short) 2, "TOSARE", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "CHIOSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "DOSARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "MOTO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "MORA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "NATO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "COREANI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "COSTINA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "TOSARE", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), (short) 2, "OSTARE", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 2, "DOTARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 2, "NITORE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 2, "CHI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 2, "RASOI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 2, "COREA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 2, "COSTI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 2, "RESTA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), (short) 2, "OSTARE", rdm.nextBoolean(),true,false));
        //return statement
        return grids.toArray(new String[grids.size()]);
    }

    private String[] createDummyGameEntries3_2(List<Player> players, UUID gameID, List<String> grids)  {
        Random rdm = new Random();
        short n=1;
        //MATCH 1
        for (String x: WordsOfGrid.GRID7.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "METTERE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TEMERE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TECA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TBC", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "METTE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "CATTURE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ACUME", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ETERE", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "RETE", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TETTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "METTE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TEMETE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "RETE", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ACUME", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "METTE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "NUTRE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "TUTTA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "RUTTA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "CATTURE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "ETERE", rdm.nextBoolean(),true,false));
        //MATCH 2
        for (String x: WordsOfGrid.GRID8.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "OLEOSO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "FAUNO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "DOSSO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ESODO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "LEONE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "SESSO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ESOSO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "OSSEO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "LENA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ANO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "NEO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "AFA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "SOLE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "NUDO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "FUNE", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "LODO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "NODO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "DOLO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "DOSE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "SODO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "NOLO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "DUNE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "DUNA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ASSOLO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "FUNE", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "DUE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "FAN", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "ASSO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "ASSE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "OSSO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "LODASSE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "SOLE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "ESSO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "NUDO", rdm.nextBoolean(),true,false));
        //MATCH 3
        for (String x: WordsOfGrid.GRID9.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "SPANNA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "CANAPO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "POSATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "PANNA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "NASPO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "SPOSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "PASSO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "BASSO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "CANNA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TASSO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TASSA", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "CASSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "RATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "CASA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TANA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "POSTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TAPPO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "BANNA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "SANTA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TASSO", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "POSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "ASPO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "ANTA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "CASO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "OSSO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "POP", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "BOSS", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "RANA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "OSSA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "TASSO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "TASSA", rdm.nextBoolean(),true,false));
        //return statement
        return grids.toArray(new String[grids.size()]);
    }

    private String[] createDummyGameEntries4_1(List<Player> players, UUID gameID, List<String> grids) {
        Random rdm = new Random();
        short n=1;
        //MATCH 1
        for (String x: WordsOfGrid.GRID2.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "MANSARDA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "MANDARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "MANDATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ANSARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "DURATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "SARDA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "DATA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "SETA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TENDA", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "STRADA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "AMANTE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "RATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ARTE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ANATRE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "STRADE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "RASATA", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "DAMA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "DARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "RADA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "DATE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "RATA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "MANTRA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "RASATA", rdm.nextBoolean(),true,false));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "DATARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "MADRE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "MANATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "AURA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "ERTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "TESA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "SETA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "STRADE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "TENDA", rdm.nextBoolean(),true,false));
        //MATCH 2
        for (String x: WordsOfGrid.GRID3.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "PIANO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "PIO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "MAIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ANO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "PUOI", rdm.nextBoolean(),false,true));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "CONO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "AIUOLA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "PALCO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "PAIA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "LANA", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "PIANA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "POI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "PUOI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "ALBO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "LANA", rdm.nextBoolean(),true,false));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "BOIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "CLAN", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "PALO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "ALPI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "CON", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "ALBO", rdm.nextBoolean(),true,false));
        //MATCH 3
        for (String x: WordsOfGrid.GRID12.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "SINCERO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "RESINA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "NERO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "RENI", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "CERO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "NEO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "REO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "CIFRE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "BICI", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "INFERO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "FECI", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "CERO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ROSE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ORSI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "PERSO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "FINE", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "BICI", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "SPINA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "SPIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "PERSO", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "OREFICI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "PERNICI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "FINE", rdm.nextBoolean(),true,false));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "PERSO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "SIERO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "PESO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "PANCINE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "FRENI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "FINE", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "BICI", rdm.nextBoolean(),true,false));
        //MATCH 4
        for (String x: WordsOfGrid.GRID6.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "REGIME", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "FINE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "RENI", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TIR", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "NERI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "FINGE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "FINGI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TIR", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "REGNI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "NERI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "GENI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "TINGE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "TIR", rdm.nextBoolean(),true,false));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "NERI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "NEI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "MIE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "TIR", rdm.nextBoolean(),true,false));
        //return statement
        return grids.toArray(new String[grids.size()]);
    }

    private String[] createDummyGameEntries5_1(List<Player> players, UUID gameID, List<String> grids) {
        Random rdm = new Random();
        short n=1;
        //MATCH 1
        for (String x: WordsOfGrid.GRID10.getGrid()) {
            grids.add(x);
        }
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "POETA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "NOVA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "VANO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "VETO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "NOVE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TOPI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TOT", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ATEO", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "VOTO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "VATE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "RIPA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "NOVE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TIR", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ATEO", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "NOTO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "TOP", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "TIPO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "NOVE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "TIR", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "ANO", rdm.nextBoolean(),true,false));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "NOTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "EVO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "AVO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "TOPI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "TOT", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "ANO", rdm.nextBoolean(),true,false));
        //P4
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "NATO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "RITO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "IRTO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "VOTI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "NOVE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "ATEO", rdm.nextBoolean(),true,false));
        //MATCH 2
        for (String x: WordsOfGrid.GRID11.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "BEVUTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TABE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "RUDE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "RUDERE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "UVA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ARTE", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ERTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "BERE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "EREDE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "UNTA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "UVA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ARTE", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "RETE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "VATE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "BEARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "BREVE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "AVERE", rdm.nextBoolean(),true,false));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "ERBA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "ETERE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "BARBA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "VERE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "VEDE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "ARTE", rdm.nextBoolean(),true,false));
        //P4
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "ERA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "DUE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "ETERA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "AUREA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "AVRETE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "AVERE", rdm.nextBoolean(),true,false));
        //MATCH 3
        for (String x: WordsOfGrid.GRID9.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "SPANNA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "CANAPO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "POSATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "CASSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TANA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "OSSA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "NASSA", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "OSSO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "NASO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "NASPO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "OSANNA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TASSA", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "RATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "PASSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "POP", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "CANTA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "NASSA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "TASSO", rdm.nextBoolean(),true,false));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "RASO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "CANNA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "SPOSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "NATA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "TASSA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "TASSO", rdm.nextBoolean(),true,false));
        //P4
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "ANTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "ANSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "BASSO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "RANA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "NASSA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "TASSO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "TASSA", rdm.nextBoolean(),true,false));
        //MATCH 4
        for (String x: WordsOfGrid.GRID3.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "AIUOLA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "PIANO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "BOIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "LANA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "IO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "PAIO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "PIANA", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "PALCA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "NAIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ANA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "NOA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "PIANA", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "PALA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "CLAN", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "POI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "PAIA", rdm.nextBoolean(),false,true));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "ALBO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "CONO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "PAIA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "ALPI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "CON", rdm.nextBoolean(),false,true));
        //P4
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "ANO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "CON", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "POI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "PUOI", rdm.nextBoolean(),false,true));
        //MATCH 5
        for (String x: WordsOfGrid.GRID7.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "METTERE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TEMERE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "TETTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ACUME", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ETERE", rdm.nextBoolean(),false,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TECA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TBC", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TUTTA", rdm.nextBoolean(),false,true));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "RETE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "NUTRE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "CATTURE", rdm.nextBoolean(),false,true));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "RUTTA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "TUTTA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "TEMETE", rdm.nextBoolean(),false,true));
        //P4
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "CATTURE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "TEMETE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "TUTTA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "METTE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "MERE", rdm.nextBoolean(),false,true));
        //MATCH 6
        for (String x: WordsOfGrid.GRID0.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "LISTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "PISTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "STIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "SLIP", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "ISTINTI", rdm.nextBoolean(),false,true));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "FINE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "INTIMI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "FINTA", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "TISI", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "LIMITA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "LIMITI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "TINTI", rdm.nextBoolean(),false,true));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "TINTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "PENTITI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "ISTINTI", rdm.nextBoolean(),false,true));
        //P4
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "PETI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "PENTITI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "FINTA", rdm.nextBoolean(),true,false));
        //return statement
        return grids.toArray(new String[grids.size()]);
    }

    private String[] createDummyGameEntries6_1(List<Player> players, UUID gameID, List<String> grids) {
        Random rdm = new Random();
        short n=1;
        //MATCH 1
        for (String x: WordsOfGrid.GRID2.getGrid()) {
            grids.add(x);
        }
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "MANSARDA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "MANDARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "MANDATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "MANTRA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "STRADE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "AURA", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "DURATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "STRADA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "TANA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ANTA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "ANSA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "AURA", rdm.nextBoolean(),true,false));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "ANDARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "DATARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "STARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "RUDE", rdm.nextBoolean(),false,false));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "RASATE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "DAMA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "DARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "MANTRA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "STRADE", rdm.nextBoolean(),false,true));
        //P4
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "ANDATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "DUE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "RADA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "TANA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "ANTA", rdm.nextBoolean(),true,false));
        //P5
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "MADRE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "DATA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "MANE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "ANSA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "AURA", rdm.nextBoolean(),true,false));
        //MATCH 2
        for (String x: WordsOfGrid.GRID4.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "SCIARADA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "NASCITA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "CALARSI", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "DRASTICA", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "FALSATI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "FLAN", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "RADA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "BUFALAI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "SALATI", rdm.nextBoolean(),false,true));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "CIALDA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "FASCIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "BASTIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "FLAN", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "RADA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "FALSATI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "SCALDA", rdm.nextBoolean(),false,true));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "LANCIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "CIARLA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "FALDA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "BAIA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "SALA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "CASATI", rdm.nextBoolean(),false,true));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "TARSIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "DARSI", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "BAITA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "ASCIA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "BAIA", rdm.nextBoolean(),true,false));
        //P4
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "SCALA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "NASCITA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "CALARSI", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "SALA", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "MALATI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "CALDA", rdm.nextBoolean(),false,true));
        //P5
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "CASTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "TASCA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "TIARA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "FLAN", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "SLANCIA", rdm.nextBoolean(),false,true));
        //MATCH 3
        for (String x: WordsOfGrid.GRID5.getGrid()) {
            grids.add(x);
        }
        n++;
        //P0
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "CHIOSARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "COSTARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "OSTINARE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "OSTARE", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(0).getPlayerID(), n, "RASO", rdm.nextBoolean(),true,false));
        //P1
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "DOSARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "NITORE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "RESTO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(1).getPlayerID(), n, "COSTINA", rdm.nextBoolean(),false,true));
        //P2
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "ROSTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "COSTA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "RASO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(2).getPlayerID(), n, "MORSE", rdm.nextBoolean(),false,true));
        //P3
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "MORSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "REATO", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "RESTO", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(3).getPlayerID(), n, "DORATI", rdm.nextBoolean(),false,true));
        //P4
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "CORSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "STARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "OSTARE", rdm.nextBoolean(),true,false));
        generatedEntries.add(new GameEntry(gameID, players.get(4).getPlayerID(), n, "RASO", rdm.nextBoolean(),true,false));
        //P5
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "DOTARE", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "CHIOSA", rdm.nextBoolean(),false,false));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "OSTINARE", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "COREANI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "CORANI", rdm.nextBoolean(),false,true));
        generatedEntries.add(new GameEntry(gameID, players.get(5).getPlayerID(), n, "RESTA", rdm.nextBoolean(),false,true));

        //return statement
        return grids.toArray(new String[grids.size()]);
    }

    private String[] createDummyGameEntries(int numPlayers, UUID gameID, int n)  {
        List<String> grids = new ArrayList<>();
        Player chosen;
        List<Player> chosenPlayers = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            chosen = generatedPlayers.get(random.nextInt(generatedPlayers.size()));
            chosenPlayers.add(chosen);
            generatedPlayers.remove(chosen);
        }
        //Put back the players extracted in the original list
        generatedPlayers.addAll(chosenPlayers);
        switch (n){
            case(1) -> createDummyGameEntries2_1(chosenPlayers, gameID, grids);
            case(2) -> createDummyGameEntries2_2(chosenPlayers, gameID, grids);
            case(3) -> createDummyGameEntries3_1(chosenPlayers, gameID, grids);
            case(4) -> createDummyGameEntries3_2(chosenPlayers, gameID, grids);
            case(5) -> createDummyGameEntries4_1(chosenPlayers, gameID, grids);
            case(6) -> createDummyGameEntries5_1(chosenPlayers, gameID, grids);
            case(7) -> createDummyGameEntries6_1(chosenPlayers, gameID, grids);
        }
        return grids.toArray(new String[grids.size()]);
    }
}
