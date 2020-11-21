package uninsubria.server.db.statistics;

import uninsubria.server.dice.DiceSet;
import uninsubria.server.dice.DiceSetStandard;
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
 * @version 0.9.0
 */
public class StatisticsManager {
    /*---Fields---*/
    private Connection connection;

    /**
     * Query for selecting the player with the top score per match.
     */
    private final String topPlayerPerMatch = "SELECT PLAYERID, SUM_MATCH AS MAX_MATCH_SCORE " +
                                             "FROM " +
                                             "(SELECT PLAYERID, GAME, MATCH, SUM(POINTS) AS SUM_MATCH " +
                                             "FROM GAMEENTRY " +
                                             "GROUP BY GAMEENTRY.PLAYERID, GAMEENTRY.GAME, GAMEENTRY.MATCH" +
                                             ") MATCH_TOTALS " +
                                             "WHERE SUM_MATCH = ("+
                                             "SELECT MAX(SUM_MATCH) " +
                                             "FROM (SELECT PLAYERID, GAME, MATCH, SUM(POINTS) AS SUM_MATCH " +
                                             "FROM GAMEENTRY " +
                                             "GROUP BY GAMEENTRY.PLAYERID, GAMEENTRY.GAME, GAMEENTRY.MATCH" +
                                             ") MATCH_TOTALS)";
    /**
     * Query for selecting the player with the top score per game.
     */
    private final String topPlayerPerGame = "SELECT PLAYERID, SUM_GAME AS MAX_GAME_SCORE " +
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
    /**
     * Query for selecting the player with the highest average score per match.
     */
    private final String topPlayerAvgMatch = "SELECT PLAYERID, AVG_SCORE_MATCH " +
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
    /**
     * Query for selecting the player with the highest average score per game.
     */
    private final String topPlayerAvgGame = "SELECT PLAYERID, AVG_SCORE_GAME " +
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
    /**
     * Query for selecting the player with the highest number of games played.
     */
    private final String topPlayerNGames = "SELECT PLAYERID, TOTAL_GAMES " +
                                            "FROM " +
                                            "(SELECT DISTINCT PLAYERID, COUNT(DISTINCT GAME) AS TOTAL_GAMES " +
                                            "FROM GAMEENTRY " +
                                            "GROUP BY PLAYERID) GAMES_PER_PLAYER " +
                                            "WHERE TOTAL_GAMES = (SELECT MAX(TOTAL_GAMES) " +
                                            "FROM (SELECT DISTINCT PLAYERID, COUNT(DISTINCT GAME) as TOTAL_GAMES " +
                                            "FROM GAMEENTRY " +
                                            "GROUP BY PLAYERID) GAMES_PER_PLAYER)";
    /**
     * Query for selecting the player with the highest record of duplicated words.
     */
    private final String topDuplicated = "SELECT PLAYERID, DUPL_OCCURR AS DUPLICATED_RECORD " +
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
    /**
     * Query for selecting the player with the highest record of wrong words.
     */
    private final String topWrong = "SELECT PLAYERID, WRONG_OCCURR WRONG_RECORD " +
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
    /**
     * Query for obtaining a ranking of the valid word occurrences.
     */
    private final String validWordsRanking = "SELECT DISTINCT Word, COUNT(*) as word_occurences " +
                                             "FROM GAMEENTRY " +
                                             "WHERE Duplicated='0' AND Wrong='0' " +
                                             "GROUP BY Word " +
                                             "ORDER BY COUNT(*) DESC";
    /**
     * Query for obtaining a ranking of the requested word occurrences.
     */
    private final String requestedWordsRanking = "SELECT DISTINCT word, COUNT(*) as Definition_Request " +
                                                 "FROM GameEntry " +
                                                 "WHERE Requested='1' " +
                                                 "GROUP BY word " +
                                                 "ORDER BY COUNT(*) DESC";
    /**
     * Query for getting all the games where a specified word was requested.
     */
    private final String getGamesWhereRequested = "SELECT DISTINCT Game " +
                                                  "FROM GAMEENTRY " +
                                                  "WHERE word=? AND Requested=TRUE";
    /**
     * Query that returns for each class of number of players in a game the minimum and maximum number of matches.
     */
    private final String minMaxMatches = "SELECT NUM_PLAYERS, MIN(NUM_MATCHES) AS MIN_NUMBER_MATCHES," +
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
    /**
     * Query that returns for each class of number of players in a game the average number of matches.
     */
    private final String avgMatches = "SELECT NUM_PLAYERS, AVG(NUM_MATCHES) AS AVG_NUMBER_MATCHES " +
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
    /**
     * Query that returns the words with the maximum number of points associated and corresponding games.
     */
    private final String gameWordMaxPoints = "SELECT GAME, WORD, POINTS " +
                                            "FROM GAMEENTRY " +
                                            "WHERE (GAME, POINTS) IN " +
                                            "(SELECT GAME, MAX(Points) AS MAX_POINTS " +
                                            "FROM GAMEENTRY " +
                                            "GROUP BY Game)";
    /**
     * Query that returns all grids associated with the given language in the GameInfo table.
     */
    private final String allGridsPerLanguage = "SELECT GRID " + "FROM GAMEINFO " +
                                                "WHERE LANGUAGE=?";

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
     * @throws SQLException
     */
    public ServiceResultInterface topPlayerMatch() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(topPlayerPerMatch);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Highest score per match");
        Result<String> player_name;
        Result<Integer> player_score;
        int i = 0;
        while (rs.next()) {
            i++;
            player_name = new Result<String>("Player_ID" + i, rs.getString(1));
            player_score = new Result<Integer>("Score" + i, rs.getInt(2));
            result.addResult(player_name);
            result.addResult(player_score);
        }
        return result;
    }

    /**
     * Gets the player that has the highest score per game. If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface topPlayerGame() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(topPlayerPerGame);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Highest score per game");
        Result<String> player_name;
        Result<Integer> player_score;
        int i = 0;
        while (rs.next()) {
            i++;
            player_name = new Result<String>("Player_ID" + i, rs.getString(1));
            player_score = new Result<Integer>("Score" + i, rs.getInt(2));
            result.addResult(player_name);
            result.addResult(player_score);
        }
        return result;
    }

    /**
     * Gets the player that has the highest average score per match. If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface topPlayerMatchAvg() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(topPlayerAvgMatch);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Highest average score per match");
        Result<String> player_name;
        Result<Double> player_score;
        int i = 0;
        while (rs.next()) {
            i++;
            player_name = new Result<String>("Player_ID" + i, rs.getString(1));
            player_score = new Result<Double>("Score" + i, rs.getDouble(2));
            result.addResult(player_name);
            result.addResult(player_score);
        }
        return result;
    }

    /**
     * Gets the player that has the highest average score per game. If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface topPlayerGameAvg() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(topPlayerAvgGame);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Highest average score per game");
        Result<String> player_name;
        Result<Double> player_score;
        int i = 0;
        while (rs.next()) {
            i++;
            player_name = new Result<String>("Player_ID" + i, rs.getString(1));
            player_score = new Result<Double>("Score" + i, rs.getDouble(2));
            result.addResult(player_name);
            result.addResult(player_score);
        }
        return result;
    }

    /**
     * Gets the player that has the highest record of played games. If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface topPlayerGamesN() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(topPlayerNGames);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Games played record");
        Result<String> player_name;
        Result<Integer> player_score;
        int i = 0;
        while (rs.next()) {
            i++;
            player_name = new Result<String>("Player_ID" + i, rs.getString(1));
            player_score = new Result<Integer>("Games" + i, rs.getInt(2));
            result.addResult(player_name);
            result.addResult(player_score);
        }
        return result;
    }

    /**
     * Gets the player that has the highest record of duplicated words PER GAME. If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface topPlayerDuplicated() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(topDuplicated);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Record duplicated words");
        Result<String> player_name;
        Result<Integer> player_score;
        int i = 0;
        while (rs.next()) {
            i++;
            player_name = new Result<String>("Player_ID" + i, rs.getString(1));
            player_score = new Result<Integer>("Duplicated" + i, rs.getInt(2));
            result.addResult(player_name);
            result.addResult(player_score);
        }
        return result;
    }

    /**
     * Gets the player that has the highest record of wrong words (in general). If 2 or more players have the same score
     * they are all returned.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface topPlayerWrong() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(topWrong);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Record wrong words");
        Result<String> player_name;
        Result<Integer> player_score;
        int i = 0;
        while (rs.next()) {
            i++;
            player_name = new Result<String>("Player_ID" + i, rs.getString(1));
            player_score = new Result<Integer>("Wrong" + i, rs.getInt(2));
            result.addResult(player_name);
            result.addResult(player_score);
        }
        return result;
    }

    /**
     * Produces a ranking of the occurrences for all valid words ever found.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface validWordsRanking() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(validWordsRanking);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Ranking of valid words occurrences");
        Result<String> word;
        Result<Integer> occurrences;
        int i = 0;
        while (rs.next()) {
            i++;
            word = new Result<String>("Word" + i, rs.getString(1));
            occurrences = new Result<Integer>("Occurrences" + i, rs.getInt(2));
            result.addResult(word);
            result.addResult(occurrences);
        }
        return result;
    }

    /**
     * Produces a ranking of the occurrences for all requested words.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface requestedWordsRanking() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(requestedWordsRanking);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Ranking of requested words occurrences");
        Result<String> word;
        Result<Integer> occurrences;
        int i = 0;
        while (rs.next()) {
            i++;
            word = new Result<String>("Word" + i, rs.getString(1));
            occurrences = new Result<Integer>("Occurrences" + i, rs.getInt(2));
            result.addResult(word);
            result.addResult(occurrences);
        }
        return result;
    }

    /**
     * Gets all gameIDs where a given word was requested.
     *
     * @param word the word requested
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface getGameByRequestedWord(String word) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(getGamesWhereRequested);
        statement.setString(1, word);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Games where the word was requested");
        Result<UUID> games;
        int i = 0;
        while (rs.next()) {
            i++;
            games = new Result<UUID>("GameID" + i, rs.getObject(1, UUID.class));
            result.addResult(games);
        }
        return result;
    }

    /**
     * For each number of players, returns the minimum and maximum number of matches.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface minMaxMatchesPerNPlayers() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(minMaxMatches);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Minimum and maximum matches for games with different number of players");
        Result<Short> n_players;
        Result<Integer> min_matches;
        Result<Integer> max_matches;
        int i = 0;
        while (rs.next()) {
            i++;
            n_players = new Result<Short>("PlayersN" + i, rs.getShort(1));
            min_matches = new Result<Integer>("Min matches" + i, rs.getInt(2));
            max_matches = new Result<Integer>("Max matches" + i, rs.getInt(3));
            result.addResult(n_players);
            result.addResult(min_matches);
            result.addResult(max_matches);
        }
        return result;
    }

    /**
     * For each number of players, returns the average number of matches.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface avgMatchesPerNPlayers() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(avgMatches);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Average matches for games with different number of players");
        Result<Short> n_players;
        Result<Double> avg_matches;
        int i = 0;
        while (rs.next()) {
            i++;
            n_players = new Result<Short>("PlayersN" + i, rs.getShort(1));
            avg_matches = new Result<Double>("Avg matches" + i, rs.getDouble(2));
            result.addResult(n_players);
            result.addResult(avg_matches);
        }
        return result;
    }

    /**
     * For each game, identifies the word with the maximum score and returns the word and points.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     */
    public ServiceResultInterface getGameMaxPointWords() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(gameWordMaxPoints);
        ResultSet rs = statement.executeQuery();
        ServiceResultInterface result = new ServiceResult("Game words with highest points");
        Result<UUID> game;
        Result<String> word;
        Result<Integer> points;
        int i = 0;
        while (rs.next()) {
            i++;
            game = new Result<UUID>("Game" + i, rs.getObject(1, UUID.class));
            word = new Result<String>("Word" + i, rs.getString(2));
            points = new Result<Integer>("Points" + i, rs.getInt(3));
            result.addResult(game);
            result.addResult(word);
            result.addResult(points);
        }
        return result;
    }

    /**
     * Produces the average occurrences for the letters in the grid for each language supported.
     *
     * @return A ServiceResult wrapping the results of the query
     * @throws SQLException
     * @throws IOException
     * @throws URISyntaxException
     */
    public ServiceResultInterface avgLetterOnGridOccurrences() throws SQLException, IOException, URISyntaxException {
        ServiceResult avg_occurrences = new ServiceResult("Average letter occurrences");
        /* For every language supported by the application do */
        for (Language lang : Language.values()) {
            /* Retrieve from the database all the grids of games with the current language.
            * This is necessary since grids generated can differ based on different dice sets
            * for different languages. */
            PreparedStatement statement = connection.prepareStatement(allGridsPerLanguage);
            statement.setString(1, lang.name());
            ResultSet rs = statement.executeQuery();
            List<String[]> grids = new ArrayList<>();
            while (rs.next()) {
                /* Grids are saved in the database per game. To obtain a list of grids for every match
                * every game grid is divided by 16 */
                String[] gameGrid = (String[]) rs.getArray(1).getArray();
                int chunks = gameGrid.length / 16;
                int j = 0;
                for (int i = 0; i < chunks; i++) {
                    grids.add(Arrays.copyOfRange(gameGrid, j, j+16));
                    j += 16;
                }
            }
            /* Retrieves all the possible letters that can appear on the grid based on the language
            * and the dice set */
            DiceSet diceSet = new DiceSet(DiceSetStandard.valueOf(lang.name()));
            String[] factors = diceSet.getLettersOccurrences();
            /* Counts occurrences for every grid */
            Hashtable<String, Integer> abs_occurr = new Hashtable<>();
            for (String f : factors) {
                abs_occurr.put(f, 0);
            }
            for (String[] grid : grids) {
                for (String letter : grid) {
                    abs_occurr.merge(letter, 1, Integer::sum);
                }
            }
            /* Obtains average occurrences with the size of the grids list */
            Hashtable<String, Double> avg_occurr = new Hashtable<>();
            for (Map.Entry<String, Integer> entry : abs_occurr.entrySet()) {
                avg_occurr.put(entry.getKey(), entry.getValue().doubleValue() / grids.size());
            }
            Result<Hashtable<String, Double>> res = new Result<>(lang.name(), avg_occurr);
            avg_occurrences.addResult(res);
        }
        return avg_occurrences;
    }
}
