package uninsubria.server.dbpopulator;

import uninsubria.server.db.api.ConnectionPool;
import uninsubria.server.db.businesslayer.GameEntry;
import uninsubria.server.db.businesslayer.GameInfo;
import uninsubria.server.db.dao.*;
import uninsubria.server.dictionary.loader.DictionaryException;
import uninsubria.utils.business.Player;
import uninsubria.utils.languages.Language;
import uninsubria.utils.security.PasswordEncryptor;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

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

    /**
     *
     * @param length
     * @return generatedString, usata per la creazione di stringhe alfanumeriche casuali
     */
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

    /**
     * il metodo elimina tutte le tuple del database
     * @throws SQLException
     * @throws InterruptedException
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
     * il metodo si occupa della popolazione del database
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws DictionaryException
     * @throws URISyntaxException
     * @throws SQLException
     * @throws InterruptedException
     */
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
            //player.setPlayerID(generateRandomAlphaNumString(20));
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

    private void createDummyGames() throws DictionaryException, IOException, URISyntaxException, NoSuchAlgorithmException {
        Language[] languages = Language.values();
        for (int i = 1; i <= 7/*gameInfoEntries*/; i++) {
            GameInfo gameInfo = new GameInfo();
            gameInfo.setGameId(UUID.randomUUID());
            gameInfo.setRuleset("STANDARD");
            /*if (generatedPlayers.size() >= 6) {
                gameInfo.setNumPlayers((byte) (random.ints(1,2,6).findFirst().getAsInt()));
            } else {
                gameInfo.setNumPlayers((byte) (random.ints(1, 2, generatedPlayers.size()).findFirst().getAsInt()));
            }*/
            switch (i){
                case(1) -> gameInfo.setNumPlayers((byte) (2));
                case(2) -> gameInfo.setNumPlayers((byte) (2));
                case(3) -> gameInfo.setNumPlayers((byte) (3));
                case(4) -> gameInfo.setNumPlayers((byte) (3));
                case(5) -> gameInfo.setNumPlayers((byte) (4));
                case(6) -> gameInfo.setNumPlayers((byte) (5));
                case(7) -> gameInfo.setNumPlayers((byte) (6));
            }
            //gameInfo.setLanguage(languages[random.nextInt(languages.length)]);
            gameInfo.setLanguage(Language.ITALIAN);

            short matchesNumber = (short) (random.nextInt(6)+1);
            //String[] grid = createDummyGameEntries(matchesNumber, gameInfo.getNumPlayers(), gameInfo.getGameId(), gameInfo.getLanguage());

            String[] grid = createDummyGameEntries(gameInfo.getNumPlayers(), gameInfo.getGameId(), gameInfo.getLanguage(), i);
            gameInfo.setAllMatchesGrid(grid);
            generatedGames.add(gameInfo);
        }
    }

   /* private String[] createDummyGameEntries(short numberOfMatches, int numPlayers, UUID gameID, Language lang) throws IOException,
            DictionaryException, URISyntaxException {
        /* Randomly extract players from the player list (no duplicates allowed)
        List<Player> chosenPlayers = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            Player chosen = generatedPlayers.get(random.nextInt(generatedPlayers.size()));
            chosenPlayers.add(chosen);
            generatedPlayers.remove(chosen);
        }
        /* Put back the players extracted in the original list
        generatedPlayers.addAll(chosenPlayers);
        /* For each match and each player generate some words - valid and not
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
            /* Add all wrong words as game entries for a random player
            for (String wrongW : foundRandomWords) {
                Player player = chosenPlayers.get(random.nextInt(chosenPlayers.size()));
                GameEntry gameEntry = new GameEntry(gameID, player.getPlayerID(), (short) matchNo, wrongW,
                        random.nextBoolean(), false, true);
                gameEntries.add(gameEntry);
            }
            /* Pick a certain number of valid found words to be duplicated
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
            /* Put remaining words
            for (String word: foundValidWords) {
                Player player = chosenPlayers.get(random.nextInt(chosenPlayers.size()));
                GameEntry gameEntry = new GameEntry(gameID, player.getPlayerID(), (short) matchNo, word,
                        random.nextBoolean(), false, false);
                gameEntries.add(gameEntry);
            }
        }
        generatedEntries.addAll(gameEntries);
        return uniqueGameGrid;
    }*/

    public List<Player> getGeneratedPlayers() {
        return generatedPlayers;
    }

    public List<GameEntry> getGeneratedEntries() {
        return generatedEntries;
    }

    public List<GameInfo> getGeneratedGames() {
        return generatedGames;
    }

    /**
     *
     * @param players
     * @param gameID
     * @param lang
     * @param grids
     * @return grids, le griglie di gioco, partita di 2 giocatori
     * @throws IOException
     * @throws DictionaryException
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     */
    private String[] createDummyGameEntries2_1(List<Player> players, UUID gameID, Language lang, List<String> grids) throws IOException,
            DictionaryException, URISyntaxException, NoSuchAlgorithmException {
        Random rdm = new Random();
        //MATCH 1
        for (String x: wordsOfGrid.GRID0.getGrid()) {
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
        for (String x: wordsOfGrid.GRID11.getGrid()) {
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
        for (String x: wordsOfGrid.GRID10.getGrid()) {
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
        for (String x: wordsOfGrid.GRID6.getGrid()) {
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
        for (String x: wordsOfGrid.GRID1.getGrid()) {
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

    /**
     *
     * @param players
     * @param gameID
     * @param lang
     * @param grids
     * @return grids, le griglie di gioco, partita di 2 giocatori
     * @throws IOException
     * @throws DictionaryException
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     */
    private String[] createDummyGameEntries2_2(List<Player> players, UUID gameID, Language lang, List<String> grids) throws IOException,
            DictionaryException, URISyntaxException, NoSuchAlgorithmException {
        Random rdm = new Random();
        //MATCH 1
        for (String x: wordsOfGrid.GRID2.getGrid()) {
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
        for (String x: wordsOfGrid.GRID2.getGrid()) {
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

    /**
     *
     * @param players
     * @param gameID
     * @param lang
     * @param grids
     * @return grids, le griglie di gioco, partita di 3 giocatori
     * @throws IOException
     * @throws DictionaryException
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     */
    private String[] createDummyGameEntries3_1(List<Player> players, UUID gameID, Language lang, List<String> grids) throws IOException,
            DictionaryException, URISyntaxException, NoSuchAlgorithmException {
        Random rdm = new Random();
        //MATCH 1
        for (String x: wordsOfGrid.GRID4.getGrid()) {
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
        for (String x: wordsOfGrid.GRID5.getGrid()) {
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

    /**
     *
     * @param players
     * @param gameID
     * @param lang
     * @param grids
     * @return grids, le griglie di gioco, partita di 3 giocatori
     * @throws IOException
     * @throws DictionaryException
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     */
    private String[] createDummyGameEntries3_2(List<Player> players, UUID gameID, Language lang, List<String> grids) throws IOException,
            DictionaryException, URISyntaxException, NoSuchAlgorithmException {
        Random rdm = new Random();
        short n=1;
        //MATCH 1
        for (String x: wordsOfGrid.GRID7.getGrid()) {
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
        for (String x: wordsOfGrid.GRID8.getGrid()) {
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
        for (String x: wordsOfGrid.GRID9.getGrid()) {
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

    /**
     *
     * @param players
     * @param gameID
     * @param lang
     * @param grids
     * @return grids, le griglie di gioco, partita di 4 giocatori
     * @throws IOException
     * @throws DictionaryException
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     */
    private String[] createDummyGameEntries4_1(List<Player> players, UUID gameID, Language lang, List<String> grids) throws IOException,
            DictionaryException, URISyntaxException, NoSuchAlgorithmException {
        Random rdm = new Random();
        short n=1;
        //MATCH 1
        for (String x: wordsOfGrid.GRID2.getGrid()) {
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
        for (String x: wordsOfGrid.GRID3.getGrid()) {
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
        for (String x: wordsOfGrid.GRID12.getGrid()) {
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
        for (String x: wordsOfGrid.GRID6.getGrid()) {
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

    /**
     *
     * @param players
     * @param gameID
     * @param lang
     * @param grids
     * @return grids, le griglie di gioco, partita a 5 giocatori
     * @throws IOException
     * @throws DictionaryException
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     */
    private String[] createDummyGameEntries5_1(List<Player> players, UUID gameID, Language lang, List<String> grids) throws IOException,
            DictionaryException, URISyntaxException, NoSuchAlgorithmException {
        Random rdm = new Random();
        short n=1;
        //MATCH 1
        for (String x: wordsOfGrid.GRID10.getGrid()) {
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
        for (String x: wordsOfGrid.GRID11.getGrid()) {
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
        for (String x: wordsOfGrid.GRID9.getGrid()) {
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
        for (String x: wordsOfGrid.GRID3.getGrid()) {
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
        for (String x: wordsOfGrid.GRID7.getGrid()) {
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
        for (String x: wordsOfGrid.GRID0.getGrid()) {
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

    /**
     *
     * @param players
     * @param gameID
     * @param lang
     * @param grids
     * @return grids, le griglie di gioco, partita a 6 giocatori
     * @throws IOException
     * @throws DictionaryException
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     */
    private String[] createDummyGameEntries6_1(List<Player> players, UUID gameID, Language lang, List<String> grids) throws IOException,
            DictionaryException, URISyntaxException, NoSuchAlgorithmException {
        Random rdm = new Random();
        short n=1;
        //MATCH 1
        for (String x: wordsOfGrid.GRID2.getGrid()) {
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
        for (String x: wordsOfGrid.GRID4.getGrid()) {
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
        for (String x: wordsOfGrid.GRID5.getGrid()) {
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

    /**
     *
     * @param numPlayers
     * @param gameID
     * @param lang
     * @param n
     * @return grids, le griglie di gioco
     * @throws IOException
     * @throws DictionaryException
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     */
    private String[] createDummyGameEntries(int numPlayers, UUID gameID, Language lang, int n) throws IOException,
            DictionaryException, URISyntaxException, NoSuchAlgorithmException {
        List<String> grids = new ArrayList<>();
        String[] arrayGrids;
        Player chosen;
        List<Player> chosenPlayers = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            chosen = generatedPlayers.get(random.nextInt(generatedPlayers.size()));
            chosenPlayers.add(chosen);
            generatedPlayers.remove(chosen);
        }
        //Put back the players extracted in the original list
        generatedPlayers.addAll(chosenPlayers);
        System.out.println("Players generated");
        switch (n){
            case(1) -> arrayGrids = createDummyGameEntries2_1(chosenPlayers, gameID, lang, grids);
            case(2) -> arrayGrids = createDummyGameEntries2_2(chosenPlayers, gameID, lang, grids);
            case(3) -> arrayGrids = createDummyGameEntries3_1(chosenPlayers, gameID, lang, grids);
            case(4) -> arrayGrids = createDummyGameEntries3_2(chosenPlayers, gameID, lang, grids);
            case(5) -> arrayGrids = createDummyGameEntries4_1(chosenPlayers, gameID, lang, grids);
            case(6) -> arrayGrids = createDummyGameEntries5_1(chosenPlayers, gameID, lang, grids);
            case(7) -> arrayGrids = createDummyGameEntries6_1(chosenPlayers, gameID, lang, grids);
        }
        return grids.toArray(new String[grids.size()]);
    }

    /*/**
     *il metodo crea tutte le tuple di un Game e le genera simulando match per match
     * @param numPlayers
     * @param gameID
     * @param lang
     * @return
     * @throws IOException
     * @throws DictionaryException
     * @throws URISyntaxException
     */
    //togliere i file gestire il tutto in maniera statica(classi enumerative)
    /*private String[] createDummyGameEntries(int numPlayers, UUID gameID, Language lang)throws IOException,
            DictionaryException, URISyntaxException {
        /* Randomly extract players from the player list (no duplicates allowed)
        List<Player> chosenPlayers = new ArrayList<>(numPlayers);
        int[] score= new int[numPlayers];
        for (int x: score)
            x = 0;
        short match_number=0;
        Player chosen;

        for (int i = 0; i < numPlayers; i++) {
            chosen = generatedPlayers.get(random.nextInt(generatedPlayers.size()));
            chosenPlayers.add(chosen);
            generatedPlayers.remove(chosen);
        }
        /* Put back the players extracted in the original list
        generatedPlayers.addAll(chosenPlayers);
        System.out.println("Players generated");
        List<String> grid = new ArrayList<>();
        List<String> totalWordInMatch =  new ArrayList<>();


        while(!isMatchFinished(score)){
            wordsOfGrid woG = wordsOfGrid.getRandomGrid();
            grid.add(woG.getGrid());
            totalWordInMatch.addAll(woG.getWords());
            match_number++;
            System.out.println("MATCH " + match_number + "     GRID: [" + woG.getGrid() + "]");
            System.out.println("Set up valid words of Match" + match_number + " completed: num players = " + numPlayers);
            ArrayList<String>[] paroleIdentificabili = new ArrayList[numPlayers];
            List<WordAnalyzer> paroleTrovate = new ArrayList<>();
            List<String> soloParoleTrovate = new ArrayList<>();
            List<WordAnalyzer>[] personalWords = new ArrayList[numPlayers];

            //si decide tutto qui il numero di parole e le parole scelte
            generateWord(numPlayers, personalWords, paroleIdentificabili, woG, paroleTrovate, lang, soloParoleTrovate);

            System.out.println();
            System.out.println("Parole Trovate: " + paroleTrovate.toString());

            //si effettuano i dovuti controlli e poi si creano le istanze di GameEntry
            controlWord(numPlayers, chosenPlayers, personalWords, score,paroleTrovate, gameID,match_number);

            //si resettano tutte le liste per un possibile prossimo match
            reset(paroleIdentificabili,paroleTrovate, soloParoleTrovate);
            System.out.println("IL MATCH N "+ match_number + " E' FINITO");
           // break;
        }

        System.out.println("SCORE = " + Arrays.toString(score));
        String[] grids = new String[grid.size()];
        return grid.toArray(grids);
    }


    public boolean isMatchFinished(int[] score) {
        for (int x : score) {
            if (x >= 50)
                return true;
        }
        return false;
    }
    */
    /*/**
     *
     * @param numPlayers num of players "playing" the game
     * @param personalWords words(WordAnalyzer) that player found
     * @param paroleIdentificabili words
     * @param woG Grid plus possibles Word
     * @param paroleTrovate words found in a specific match without repetition
     * @param lang Language [italian]
     * Generate random word from wordsOfGrid pool
     * the method
     *
     *//*
    public void generateWord(int numPlayers, List<WordAnalyzer>[] personalWords, ArrayList<String>[] paroleIdentificabili, wordsOfGrid woG, List<WordAnalyzer> paroleTrovate, Language lang, List<String> soloParoleTrovate/*,valids*){
        for (int i=0; i<numPlayers; i++) {
            personalWords[i] = new ArrayList<>();
            paroleIdentificabili[i] = new ArrayList<>();
            if(i==0)
                paroleIdentificabili[i].addAll(woG.getWords());
            else{
                paroleIdentificabili[i].addAll(woG.getWords());
                for (int j=0; j<personalWords[i-1].size();j++){
                    if(personalWords[i-1].get(j).isValid())
                    paroleIdentificabili[i].remove(personalWords[i-1].get(j).getWord());
                }
            }
            System.out.println("parole identificabili: " + paroleIdentificabili[i].toString());
            int numWordFound = random.nextInt((5)+ 1);
            System.out.println("Player n " + i);

            for (int j = 0; j < numWordFound; j++) {
                int n =random.nextInt(paroleIdentificabili[i].size());
                String word = paroleIdentificabili[i].remove(n);
                WordAnalyzer wordAnalyzer = new WordAnalyzer(word, lang);
                wordAnalyzer.setDuplicate(false);
                if (!soloParoleTrovate.contains(word)){
                    paroleTrovate.add(wordAnalyzer);
                    soloParoleTrovate.add(word);
                }
                else{
                    for(WordAnalyzer x: paroleTrovate){
                        if(x.getWord().equals(word))
                            x.setDuplicate(true);
                    }
                }
                personalWords[i].add(wordAnalyzer);
            }
        }
    }*/

    /*/**
     *
     * @param numPlayers num of players "playing" the game
     * @param chosenPlayers Array of Players
     * @param personalWords words(WordAnalyzer) that player found
     * @param score Array of Players score
     * @param paroleTrovate words found in a specific match without repetition
     * @param gameID the game id
     * @param match_number the match number
     * the method control the word previously generated
     *//*
    public void controlWord(int numPlayers, List<Player> chosenPlayers, List<WordAnalyzer>[] personalWords, int[] score, List<WordAnalyzer> paroleTrovate, UUID gameID, short match_number/*,valids*){
        for (int i=0; i<numPlayers; i++) {
            Player chosen = chosenPlayers.get(i);
            //CONTROLLO delle parole duplicate in base al valore della parola in parole trovate
            for (WordAnalyzer x : paroleTrovate) {
                for (int j = 0; j < personalWords[i].size(); j++) {
                    if (x.getWord().equals(personalWords[i].get(j).getWord())) {
                        personalWords[i].get(j).setDuplicate(x.isDuplicated());
                    }
                }
            }
        }
        for (int i=0; i<numPlayers; i++) {
            Player chosen = chosenPlayers.get(i);

            for (int j=0; j<personalWords[i].size(); j++){
                String word = personalWords[i].get(j).getWord();
                if(personalWords[i].get(j).isValid() & !personalWords[i].get(j).isDuplicated())
                    score[i] += personalWords[i].get(j).getScore();

                boolean requested = random.nextBoolean();

                GameEntry gameEntry = new GameEntry(gameID, chosen.getPlayerID(), match_number, word, requested, personalWords[i].get(j).isDuplicated(), !personalWords[i].get(j).isValid());
                System.out.println("Parola: " + word + " valid: " + personalWords[i].get(j).isValid() + ", duplicated: " + personalWords[i].get(j).isDuplicated() + ", requested: " + requested + ", score : " + gameEntry.getPoints());
            }
            System.out.println("Player " + i + " score: " + score[i]);
        }
    }*/

    /*/**
     *
     * @param paroleIdentificabili
     * @param paroleTrovate
     *//*
    public void reset(List<String>[] paroleIdentificabili, List<WordAnalyzer> paroleTrovate, List<String> soloParoleTrovate/*,valids*){
        for (int i=0; i<paroleIdentificabili.length;i++)
            paroleIdentificabili[i].clear();
        paroleTrovate.clear();
        soloParoleTrovate.clear();
    }*/
}
