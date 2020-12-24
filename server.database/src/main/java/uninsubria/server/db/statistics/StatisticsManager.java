package uninsubria.server.db.statistics;

import uninsubria.server.dice.DiceSet;
import uninsubria.server.dice.DiceSetStandard;
import uninsubria.utils.business.PlayerStatResult;
import uninsubria.utils.business.TurnsResult;
import uninsubria.utils.business.WordGameStatResult;
import uninsubria.utils.languages.Language;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResult;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Class responsible for executing the queries and methods for calculating the requested statistics.
 *
 * @author Giulia Pais
 * @author Alessandro Lerro
 * @version 0.9.1
 */
public class StatisticsManager {
    /*---Fields---*/
    private Connection connection;

    /*---Constructors---*/

    /**
     * Instantiates a new StatisticsManager.
     */
    public StatisticsManager() {}

    /*---Methods---*/

    /**
     * Gets connection.
     *
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Sets connection.
     *
     * @param connection the connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets the player that has the highest score per match. If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface topPlayerMatch() throws SQLException {
        String topPlayerPerMatch = "SELECT DISTINCT PLAYERID, SUM_MATCH AS MAX_MATCH_SCORE " +
                "FROM " +
                "(SELECT PLAYERID, GAME, MATCH, SUM(POINTS) AS SUM_MATCH " +
                "FROM GAMEENTRY " +
                "GROUP BY GAMEENTRY.PLAYERID, GAMEENTRY.GAME, GAMEENTRY.MATCH" +
                ") MATCH_TOTALS " +
                "WHERE SUM_MATCH = (" +
                "SELECT MAX(SUM_MATCH) " +
                "FROM (SELECT PLAYERID, GAME, MATCH, SUM(POINTS) AS SUM_MATCH " +
                "FROM GAMEENTRY " +
                "GROUP BY GAMEENTRY.PLAYERID, GAMEENTRY.GAME, GAMEENTRY.MATCH" +
                ") MATCH_TOTALS)";
        PreparedStatement statement = connection.prepareStatement(topPlayerPerMatch);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Highest score per match");
        Result<PlayerStatResult> tuple;
        int i = 0;
        while (rs.next()) {
            i++;
            tuple = new Result<>(Integer.toString(i), new PlayerStatResult(rs.getString(1),
                    Integer.valueOf(rs.getInt(2)).doubleValue()));
            result.addResult(tuple);
        }
        return result;
    }

    /**
     * Gets the player that has the highest score per game. If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface topPlayerGame() throws SQLException {
        String topPlayerPerGame = "SELECT DISTINCT PLAYERID, SUM_GAME AS MAX_GAME_SCORE " +
                "FROM " +
                "(SELECT PLAYERID, GAME,  SUM(POINTS) AS SUM_GAME " +
                "FROM GAMEENTRY " +
                "GROUP BY GAMEENTRY.PLAYERID, GAMEENTRY.GAME" +
                ") GAME_TOTALS " +
                "WHERE SUM_GAME = (SELECT MAX(SUM_GAME) " +
                "FROM (SELECT PLAYERID, GAME,  SUM(POINTS) AS SUM_GAME " +
                "FROM GAMEENTRY " +
                "GROUP BY GAMEENTRY.PLAYERID, GAMEENTRY.GAME" +
                ") GAME_TOTALS)";
        PreparedStatement statement = connection.prepareStatement(topPlayerPerGame);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Highest score per game");
        Result<PlayerStatResult> tuple;
        int i = 0;
        while (rs.next()) {
            i++;
            tuple = new Result<>(Integer.toString(i), new PlayerStatResult(rs.getString(1),
                    Integer.valueOf(rs.getInt(2)).doubleValue()));
            result.addResult(tuple);
        }
        return result;
    }

    /**
     * Gets the player that has the highest average score per match. If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface topPlayerMatchAvg() throws SQLException {
        String topPlayerAvgMatch = "SELECT DISTINCT PLAYERID, AVG_SCORE_MATCH " +
                "FROM " +
                "(SELECT PLAYERID, AVG(SUM_MATCH) AS AVG_SCORE_MATCH " +
                "FROM " +
                "(SELECT PLAYERID, GAME, MATCH, SUM(POINTS) AS SUM_MATCH " +
                "FROM GAMEENTRY " +
                "GROUP BY GAMEENTRY.PLAYERID, GAMEENTRY.GAME, GAMEENTRY.MATCH) MATCH_TOTALS " +
                "GROUP BY PLAYERID) MATCH_AVG " +
                "WHERE AVG_SCORE_MATCH = (SELECT MAX(AVG_SCORE_MATCH) " +
                "FROM (SELECT PLAYERID, AVG(SUM_MATCH) AS AVG_SCORE_MATCH " +
                "FROM " +
                "(SELECT PLAYERID, GAME, MATCH, SUM(POINTS) AS SUM_MATCH " +
                "FROM GAMEENTRY " +
                "GROUP BY GAMEENTRY.PLAYERID, GAMEENTRY.GAME, GAMEENTRY.MATCH) MATCH_TOTALS " +
                "GROUP BY PLAYERID) MATCH_AVG)";
        PreparedStatement statement = connection.prepareStatement(topPlayerAvgMatch);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Highest average score per match");
        Result<PlayerStatResult> tuple;
        int i = 0;
        while (rs.next()) {
            i++;
            tuple = new Result<>(Integer.toString(i), new PlayerStatResult(rs.getString(1),
                    Integer.valueOf(rs.getInt(2)).doubleValue()));
            result.addResult(tuple);
        }
        return result;
    }

    /**
     * Gets the player that has the highest average score per game. If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface topPlayerGameAvg() throws SQLException {
        String topPlayerAvgGame = "SELECT DISTINCT PLAYERID, AVG_SCORE_GAME " +
                "FROM " +
                "(SELECT PLAYERID, AVG(SUM_GAME) AS AVG_SCORE_GAME " +
                "FROM " +
                "(SELECT PLAYERID, GAME,  SUM(POINTS) AS SUM_GAME " +
                "FROM GAMEENTRY " +
                "GROUP BY GAMEENTRY.PLAYERID, GAMEENTRY.GAME" +
                ") GAME_TOTALS " +
                "GROUP BY PLAYERID) GAME_AVG " +
                "WHERE AVG_SCORE_GAME = (SELECT MAX(AVG_SCORE_GAME) " +
                "FROM (SELECT PLAYERID, AVG(SUM_GAME) AS AVG_SCORE_GAME " +
                "FROM (SELECT PLAYERID, GAME,  SUM(POINTS) AS SUM_GAME " +
                "FROM GAMEENTRY " +
                "GROUP BY GAMEENTRY.PLAYERID, GAMEENTRY.GAME " +
                ") GAME_TOTALS " +
                "GROUP BY PLAYERID) GAME_AVG)";
        PreparedStatement statement = connection.prepareStatement(topPlayerAvgGame);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Highest average score per game");
        Result<PlayerStatResult> tuple;
        int i = 0;
        while (rs.next()) {
            i++;
            tuple = new Result<>(Integer.toString(i), new PlayerStatResult(rs.getString(1),
                    Integer.valueOf(rs.getInt(2)).doubleValue()));
            result.addResult(tuple);
        }
        return result;
    }

    /**
     * Gets the player that has the highest record of played games. If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface topPlayerGamesN() throws SQLException {
        String topPlayerNGames = "SELECT DISTINCT PLAYERID, TOTAL_GAMES " +
                "FROM " +
                "(SELECT DISTINCT PLAYERID, COUNT(DISTINCT GAME) AS TOTAL_GAMES " +
                "FROM GAMEENTRY " +
                "GROUP BY PLAYERID) GAMES_PER_PLAYER " +
                "WHERE TOTAL_GAMES = (SELECT MAX(TOTAL_GAMES) " +
                "FROM (SELECT DISTINCT PLAYERID, COUNT(DISTINCT GAME) as TOTAL_GAMES " +
                "FROM GAMEENTRY " +
                "GROUP BY PLAYERID) GAMES_PER_PLAYER)";
        PreparedStatement statement = connection.prepareStatement(topPlayerNGames);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Games played record");
        Result<PlayerStatResult> tuple;
        int i = 0;
        while (rs.next()) {
            i++;
            tuple = new Result<>(Integer.toString(i), new PlayerStatResult(rs.getString(1),
                    Integer.valueOf(rs.getInt(2)).doubleValue()));
            result.addResult(tuple);
        }
        return result;
    }

    /**
     * Gets the player that has the highest record of duplicated words PER GAME. If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface topPlayerDuplicated() throws SQLException {
        String topDuplicated = "SELECT DISTINCT PLAYERID, DUPL_OCCURR AS DUPLICATED_RECORD " +
                "FROM " +
                "(SELECT PLAYERID, COUNT(*) AS DUPL_OCCURR " +
                "FROM GAMEENTRY " +
                "WHERE DUPLICATED=TRUE " +
                "GROUP BY PLAYERID, GAME) DUPL " +
                "WHERE DUPL_OCCURR = (SELECT MAX(DUPL_OCCURR) " +
                "FROM (SELECT PLAYERID, COUNT(*) AS DUPL_OCCURR " +
                "FROM GAMEENTRY " +
                "WHERE DUPLICATED=TRUE " +
                "GROUP BY PLAYERID, GAME) DUPL)";
        PreparedStatement statement = connection.prepareStatement(topDuplicated);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Record duplicated words");
        Result<PlayerStatResult> tuple;
        int i = 0;
        while (rs.next()) {
            i++;
            tuple = new Result<>(Integer.toString(i), new PlayerStatResult(rs.getString(1),
                    Integer.valueOf(rs.getInt(2)).doubleValue()));
            result.addResult(tuple);
        }
        return result;
    }

    /**
     * Gets the player that has the highest record of wrong words (in general). If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface topPlayerWrong() throws SQLException {
        String topWrong = "SELECT DISTINCT PLAYERID, WRONG_OCCURR WRONG_RECORD " +
                "FROM " +
                "(SELECT PLAYERID, COUNT(*) AS WRONG_OCCURR " +
                "FROM GAMEENTRY " +
                "WHERE WRONG=TRUE " +
                "GROUP BY PLAYERID) WRONG " +
                "WHERE WRONG_OCCURR = (SELECT MAX(WRONG_OCCURR) " +
                "FROM (SELECT PLAYERID, COUNT(*) AS WRONG_OCCURR " +
                "FROM GAMEENTRY " +
                "WHERE WRONG=TRUE " +
                "GROUP BY PLAYERID) WRONG)";
        PreparedStatement statement = connection.prepareStatement(topWrong);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Record wrong words");
        Result<PlayerStatResult> tuple;
        int i = 0;
        while (rs.next()) {
            i++;
            tuple = new Result<>(Integer.toString(i), new PlayerStatResult(rs.getString(1),
                    Integer.valueOf(rs.getInt(2)).doubleValue()));
            result.addResult(tuple);
        }
        return result;
    }

    /**
     * Produces a ranking of the occurrences for all valid words ever found.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface validWordsRanking() throws SQLException {
        String validWordsRanking = "SELECT DISTINCT Word, COUNT(*) as word_occurences " +
                "FROM GAMEENTRY " +
                "WHERE Duplicated='0' AND Wrong='0' " +
                "GROUP BY Word " +
                "ORDER BY COUNT(*) DESC";
        PreparedStatement statement = connection.prepareStatement(validWordsRanking);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Ranking of valid words occurrences");
        if (rs.next()) {
            Hashtable<String, Integer> words = new Hashtable<>();
            do {
                String word = rs.getString(1);
                Integer occ = rs.getInt(2);
                words.put(word, occ);
            } while (rs.next());
            Result<Hashtable<String, Integer>> res = new Result<>("Occurrences", words);
            result.addResult(res);
        }
        return result;
    }

    /**
     * Produces a ranking of the occurrences for all requested words.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface requestedWordsRanking() throws SQLException {
        String requestedWordsRanking = "SELECT DISTINCT word, COUNT(*) as Definition_Request " +
                "FROM GameEntry " +
                "WHERE Requested='1' " +
                "GROUP BY word " +
                "ORDER BY COUNT(*) DESC";
        PreparedStatement statement = connection.prepareStatement(requestedWordsRanking);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Ranking of requested words occurrences");
        if (rs.next()) {
            Hashtable<String, Integer> words = new Hashtable<>();
            do {
                String word = rs.getString(1);
                Integer occ = rs.getInt(2);
                words.put(word, occ);
            } while (rs.next());
            Result<Hashtable<String, Integer>> res = new Result<>("Occurrences", words);
            result.addResult(res);
        }
        return result;
    }

    /**
     * Gets all gameIDs where a given word was requested.
     *
     * @param word the word requested
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface getGameByRequestedWord(String word) throws SQLException {
        String getGamesWhereRequested = "SELECT DISTINCT Game " +
                "FROM GAMEENTRY " +
                "WHERE word=? AND Requested=TRUE";
        PreparedStatement statement = connection.prepareStatement(getGamesWhereRequested);
        statement.setString(1, word);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Games where the word was requested");
        if (rs.next()) {
            int i = 0;
            do {
                Result<UUID> games = new Result<>(Integer.toString(++i), rs.getObject(1, UUID.class));
                result.addResult(games);
            } while (rs.next());
        }
        return result;
    }

    /**
     * Returns matches statistics for each category of games (2-6 players), namely minimum, maximum and average
     * number of matches.
     *
     * @return A service result wrapping the results of the queries
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface turnStats() throws SQLException {
        HashMap<Integer, Integer[]> minMax = minMaxMatchesPerNPlayers();
        HashMap<Integer, Double> avg = avgMatchesPerNPlayers();
        ServiceResult serviceResult = new ServiceResult("TURNS STATS");
        for (Integer i : minMax.keySet()) {
            TurnsResult turnsResult = new TurnsResult();
            turnsResult.setCategory(i);
            turnsResult.setMinTurns(minMax.get(i)[0]);
            turnsResult.setMaxTurns(minMax.get(i)[1]);
            turnsResult.setAvgTurns(avg.get(i));
            Result<TurnsResult> res = new Result<>(Integer.toString(i), turnsResult);
            serviceResult.addResult(res);
        }
        return serviceResult;
    }

    /**
     * For each number of players, returns the minimum and maximum number of matches.
     *
     * @return A map for each category of games
     * @throws SQLException
     */
    private HashMap<Integer, Integer[]> minMaxMatchesPerNPlayers() throws SQLException {
        String minMaxMatches = "SELECT NUM_PLAYERS, MIN(NUM_MATCHES) AS MIN_NUMBER_MATCHES," +
                "MAX(NUM_MATCHES) AS MAX_NUMBER_MATCHES " +
                "FROM " +
                "(SELECT GAME_NPLAYERS.GAMEID, NUM_MATCHES, NUM_PLAYERS " +
                "FROM " +
                "(SELECT DISTINCT GAME, COUNT(DISTINCT MATCH) AS NUM_MATCHES " +
                "FROM GAMEENTRY " +
                "GROUP BY GAME) GAME_NMATCH " +
                "INNER JOIN " +
                "(SELECT GAMEID, NUM_PLAYERS " +
                "FROM GAMEINFO) GAME_NPLAYERS " +
                "ON GAME_NMATCH.GAME = GAME_NPLAYERS.GAMEID) MATCHES_PLAYERS " +
                "GROUP BY NUM_PLAYERS";
        PreparedStatement statement = connection.prepareStatement(minMaxMatches);
        ResultSet rs = statement.executeQuery();
        HashMap<Integer, Integer[]> minMaxTurns = new HashMap<>();
        while (rs.next()) {
            int n_players = rs.getInt(1);
            int min_matches = rs.getInt(2);
            int max_matches = rs.getInt(3);
            minMaxTurns.put(n_players, new Integer[] {min_matches, max_matches});
        }
        return minMaxTurns;
    }

    /**
     * For each number of players, returns the average number of matches.
     *
     * @return A map for each category of games
     * @throws SQLException
     */
    private HashMap<Integer, Double> avgMatchesPerNPlayers() throws SQLException {
        String avgMatches = "SELECT NUM_PLAYERS, AVG(NUM_MATCHES) AS AVG_NUMBER_MATCHES " +
                "FROM " +
                "(SELECT GAME_NPLAYERS.GAMEID, NUM_MATCHES, NUM_PLAYERS " +
                "FROM " +
                "(SELECT DISTINCT GAME, COUNT(DISTINCT MATCH) AS NUM_MATCHES " +
                "FROM GAMEENTRY " +
                "GROUP BY GAME) GAME_NMATCH " +
                "INNER JOIN " +
                "(SELECT GAMEID, NUM_PLAYERS " +
                "FROM GAMEINFO) GAME_NPLAYERS " +
                "ON GAME_NMATCH.GAME = GAME_NPLAYERS.GAMEID) MATCHES_PLAYERS " +
                "GROUP BY NUM_PLAYERS";
        PreparedStatement statement = connection.prepareStatement(avgMatches);
        ResultSet rs = statement.executeQuery();
        HashMap<Integer, Double> avgTurns = new HashMap<>();
        while (rs.next()) {
           int n_players = rs.getInt(1);
           double avg = rs.getDouble(2);
           avgTurns.put(n_players, avg);
        }
        return avgTurns;
    }

    /**
     * For each game, identifies the word with the maximum score and returns the word and points.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface getGameMaxPointWords() throws SQLException {
        String gameWordMaxPoints = "SELECT DISTINCT GAME, WORD, POINTS " +
                "FROM GAMEENTRY " +
                "WHERE (GAME, POINTS) IN " +
                "(SELECT GAME, MAX(Points) AS MAX_POINTS " +
                "FROM GAMEENTRY " +
                "GROUP BY Game)";
        PreparedStatement statement = connection.prepareStatement(gameWordMaxPoints);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Game words with highest points");
        if (rs.next()) {
            int i = 0;
            do {
                UUID game = rs.getObject(1, UUID.class);
                String word = rs.getString(2);
                Integer points = rs.getInt(3);
                WordGameStatResult tuple = new WordGameStatResult(game, word, points);
                Result<WordGameStatResult> res = new Result<>(Integer.toString(++i), tuple);
                result.addResult(res);
            } while (rs.next());
        }
        return result;
    }

    /**
     * Produces the average occurrences for the letters in the grid for each language supported.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException the sql exception
     */
    public ServiceResultInterface avgLetterOnGridOccurrences() throws SQLException {
        ServiceResult avg_occurrences = new ServiceResult("Average letter occurrences");
        /* For every language supported by the application do */
        String allGridsPerLanguage = "SELECT GRID " + "FROM GAMEINFO " +
                "WHERE LANGUAGE=?";
        for (Language lang : Language.values()) {
            /* Retrieve from the database all the grids of games with the current language.
            * This is necessary since grids generated can differ based on different dice sets
            * for different languages. */
            PreparedStatement statement = connection.prepareStatement(allGridsPerLanguage);
            statement.setString(1, lang.name());
            ResultSet rs = statement.executeQuery();
            /* Retrieves all the possible letters that can appear on the grid based on the language
             * and the dice set */
            DiceSet diceSet = new DiceSet(DiceSetStandard.valueOf(lang.name()));
            String[] factors = diceSet.getLettersOccurrences();
            /* Counts occurrences for every grid */
            Hashtable<String, Integer> abs_occurr = new Hashtable<>();
            Hashtable<String, Double> avg_occurr = new Hashtable<>();
            for (String f : factors) {
                abs_occurr.put(f, 0);
            }
            List<String[]> grids = new ArrayList<>();
            if (rs.next()) {
                do {
                    /* Grids are saved in the database per game. To obtain a list of grids for every match
                     * every game grid is divided by 16 */
                    String[] gameGrid = (String[]) rs.getArray(1).getArray();
                    int chunks = gameGrid.length / 16;
                    int j = 0;
                    for (int i = 0; i < chunks; i++) {
                        grids.add(Arrays.copyOfRange(gameGrid, j, j+16));
                        j += 16;
                    }
                } while (rs.next());
                for (String[] grid : grids) {
                    for (String letter : grid) {
                        abs_occurr.merge(letter, 1, Integer::sum);
                    }
                }
                /* Obtains average occurrences with the size of the grids list */
                for (Map.Entry<String, Integer> entry : abs_occurr.entrySet()) {
                    avg_occurr.put(entry.getKey(), entry.getValue().doubleValue() / grids.size());
                }
            } else {
                for (Map.Entry<String, Integer> entry : abs_occurr.entrySet()) {
                    avg_occurr.put(entry.getKey(), 0.0);
                }
            }
            Result<Hashtable<String, Double>> res = new Result<>(lang.name(), avg_occurr);
            avg_occurrences.addResult(res);
        }
        return avg_occurrences;
    }
}
