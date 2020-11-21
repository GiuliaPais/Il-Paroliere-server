package uninsubria.server.dbpopulator;

import uninsubria.server.db.api.ConnectionPool;
import uninsubria.server.db.businesslayer.GameEntry;
import uninsubria.server.db.businesslayer.GameInfo;
import uninsubria.server.db.dao.*;
import uninsubria.server.dice.DiceSet;
import uninsubria.server.dice.DiceSetStandard;
import uninsubria.server.dictionary.loader.DictionaryException;
import uninsubria.server.dictionary.manager.DictionaryManager;
import uninsubria.utils.business.Player;
import uninsubria.utils.languages.Language;
import uninsubria.utils.security.PasswordEncryptor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Giulia Pais
 * @version 0.9.0
 */
public class DbPopulator {
    /*---Fields---*/
    private PlayerDAO playerDAO;
    private GameEntryDAO gameEntryDAO;
    private GameInfoDAO gameInfoDAO;
    private int playerEntries, gameInfoEntries;
    private List<Player> generatedPlayers;
    private List<GameEntry> generatedEntries;
    private List<GameInfo> generatedGames;
    private Random random = new Random();
    private final String[] possibleNames = new String[] {"Andrea", "Marco", "Lorenzo", "Leonardo", "Alessandro",
    "Davide", "Mattia", "Gabriele", "Luca", "Edoardo", "Riccardo", "Pietro", "Tommaso", "Matteo", "Francesco",
    "Sofia", "Giulia", "Alice", "Aurora", "Emma", "Giorgia", "Chiara", "Anna", "Beatrice", "Lucia"};
    private final String[] possibleLastNames = new String[] {"Rossi", "Bianchi", "Ferrari", "Russo", "Gallo",
    "Costa", "Fontana", "Conti", "Ricci", "Rinaldi", "Longo", "Gatti", "Galli", "Ferraro", "Santoro", "Ferri",
    "Colombo", "De Luca", "Barbieri", "Sala", "Villa", "Cattaneo", "Brambilla", "Fumagalli", "Trevisan", "Magnani"};
    private List<String> moves = new ArrayList<>(Arrays.asList("UP", "DOWN", "LEFT", "RIGHT", "RIGHTUP", "RIGHTDOWN", "LEFTUP", "LEFTDOWN"));

    /*---Constructors---*/
    public DbPopulator(int playerEntries, int gameInfoEntries) {
        this.playerDAO = new PlayerDAOImpl();
        this.gameEntryDAO = new GameEntryDAOImpl();
        this.gameInfoDAO = new GameInfoDAOImpl();
        this.playerEntries = playerEntries;
        this.gameInfoEntries = gameInfoEntries;
        this.generatedPlayers = new ArrayList<>();
        this.generatedGames = new ArrayList<>();
        this.generatedEntries = new ArrayList<>();
    }

    /*---Methods---*/
    private String generateRandomAlphaNumString(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

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

    public void populate() throws NoSuchAlgorithmException, IOException, DictionaryException, URISyntaxException, SQLException, InterruptedException {
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
            player.setPlayerID(generateRandomAlphaNumString(20));
            player.setEmail(player.getPlayerID() + "@example.com");
            player.setSurname(possibleLastNames[random.nextInt(possibleLastNames.length)]);
            player.setName(possibleNames[random.nextInt(possibleNames.length)]);
            player.setLogStatus(random.nextBoolean());
            player.setProfileImage(random.nextInt(10)+1);
            player.setPassword(PasswordEncryptor.hashPassword(generateRandomAlphaNumString(10)));
            generatedPlayers.add(player);
        }
    }

    private void createDummyGames() throws DictionaryException, IOException, URISyntaxException {
        Language[] languages = Language.values();
        for (int i = 0; i < gameInfoEntries; i++) {
            GameInfo gameInfo = new GameInfo();
            gameInfo.setGameId(UUID.randomUUID());
            gameInfo.setRuleset("STANDARD");
            if (generatedPlayers.size() >= 6) {
                gameInfo.setNumPlayers((byte) (random.ints(1,2,6).findFirst().getAsInt()));
            } else {
                gameInfo.setNumPlayers((byte) (random.ints(1, 2, generatedPlayers.size()).findFirst().getAsInt()));
            }
            gameInfo.setLanguage(languages[random.nextInt(languages.length)]);
            short matchesNumber = (short) (random.nextInt(6)+1);
            String[] grid = createDummyGameEntries(matchesNumber, gameInfo.getNumPlayers(), gameInfo.getGameId(), gameInfo.getLanguage());
            gameInfo.setAllMatchesGrid(grid);
            generatedGames.add(gameInfo);
        }
    }

    private String[] createDummyGameEntries(short numberOfMatches, int numPlayers, UUID gameID, Language lang) throws IOException,
            DictionaryException, URISyntaxException {
        /* Randomly extract players from the player list (no duplicates allowed) */
        List<Player> chosenPlayers = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            Player chosen = generatedPlayers.get(random.nextInt(generatedPlayers.size()));
            chosenPlayers.add(chosen);
            generatedPlayers.remove(chosen);
        }
        /* Put back the players extracted in the original list */
        generatedPlayers.addAll(chosenPlayers);
        /* For each match and each player generate some words - valid and not */
        List<GameEntry> gameEntries = new ArrayList<>();
        List<String> validWords = DictionaryManager.getValidWords(lang);
        DiceSet diceSet = new DiceSet();
        diceSet.setDiceSetStandard(DiceSetStandard.valueOf(lang.name()));
        String[] uniqueGameGrid = new String[16];
        for(int matchNo = 1; matchNo <= numberOfMatches; matchNo++) {
            List<String> foundValidWords = new ArrayList<>();
            List<String> foundRandomWords = new ArrayList<>();
            diceSet.setNotThrown();
            diceSet.throwDices();
            diceSet.randomizePosition();
            String[] matchGrid = diceSet.getResultFaces();
            if (matchNo == 1) {
                uniqueGameGrid = matchGrid;
            } else {
                uniqueGameGrid = Stream.concat(Arrays.stream(uniqueGameGrid), Arrays.stream(matchGrid))
                        .toArray(String[]::new);
            }
            GraphGrid graphGrid = new GraphGrid(matchGrid);
            int validWordsToFind = random.nextInt(10);
            int randomWords = random.nextInt(10);
            while (validWordsToFind > 0 & validWords.size() > 0) {
                int rand = random.nextInt(validWords.size());
                String currWord = validWords.remove(rand);
                if (currWord.length() < 3) {
                    continue;
                }
                boolean obtainable = graphGrid.isDerivableFromGrid(currWord);
                if (obtainable) {
                    foundValidWords.add(currWord);
                    validWordsToFind--;
                } else if(randomWords > 0) {
                    foundRandomWords.add(currWord);
                    randomWords--;
                }
            }
            /* Add all wrong words as game entries for a random player */
            for (String wrongW : foundRandomWords) {
                Player player = chosenPlayers.get(random.nextInt(chosenPlayers.size()));
                GameEntry gameEntry = new GameEntry(gameID, player.getPlayerID(), (short) matchNo, wrongW,
                        random.nextBoolean(), false, true);
                gameEntries.add(gameEntry);
            }
            /* Pick a certain number of valid found words to be duplicated */
            if (foundValidWords.size() > 0) {
                int duplicatedWords = random.nextInt(foundValidWords.size());
                while (duplicatedWords > 0) {
                    String duplWord = foundValidWords.remove(random.nextInt(foundValidWords.size()));
                    duplicatedWords--;
                    Player player1 = chosenPlayers.get(random.nextInt(chosenPlayers.size()));
                    Player player2 = player1;
                    while (player1.equals(player2)) {
                        player2 = chosenPlayers.get(random.nextInt(chosenPlayers.size()));
                    }
                    GameEntry gameEntry1 = new GameEntry(gameID, player1.getPlayerID(), (short) matchNo, duplWord,
                            random.nextBoolean(), true, false);
                    GameEntry gameEntry2 = new GameEntry(gameID, player2.getPlayerID(), (short) matchNo, duplWord,
                            random.nextBoolean(), true, false);
                    gameEntries.add(gameEntry1);
                    gameEntries.add(gameEntry2);
                }
            }
            /* Put remaining words */
            for (String word: foundValidWords) {
                Player player = chosenPlayers.get(random.nextInt(chosenPlayers.size()));
                GameEntry gameEntry = new GameEntry(gameID, player.getPlayerID(), (short) matchNo, word,
                        random.nextBoolean(), false, false);
                gameEntries.add(gameEntry);
            }
        }
        generatedEntries.addAll(gameEntries);
        return uniqueGameGrid;
    }

    public List<Player> getGeneratedPlayers() {
        return generatedPlayers;
    }

    public List<GameEntry> getGeneratedEntries() {
        return generatedEntries;
    }

    public List<GameInfo> getGeneratedGames() {
        return generatedGames;
    }
}
