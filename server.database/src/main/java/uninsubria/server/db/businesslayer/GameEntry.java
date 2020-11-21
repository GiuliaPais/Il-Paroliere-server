package uninsubria.server.db.businesslayer;

import java.util.UUID;

/**
 * Represents a mapping of a tuple in the GameEntry table of the database.
 *
 * @author Giulia Pais
 * @author Alessandro Lerro
 * @version 0.9.0
 */
public class GameEntry {
    /*---Fields---*/
    private UUID game;
    private String playerID;
    private short match;
    private String word;
    private boolean requested;
    private boolean duplicated;
    private boolean wrong;
    private short points;

    /*---Constructors---*/
    /**
     * Instantiates a new GameEntry.
     */
    public GameEntry() {}

    /**
     * Instantiates a new GameEntry.
     *
     * @param gameID     the game id
     * @param playerID   the player id
     * @param match      the match number
     * @param word       the word
     * @param requested  Was the word requested?
     * @param duplicated Was the word duplicated?
     * @param wrong      Was the word wrong?
     */
    public GameEntry(UUID gameID, String playerID, short match, String word, boolean requested,
                     boolean duplicated, boolean wrong) {
        this.game = gameID;
        this.playerID = playerID;
        this.match = match;
        this.word = word;
        this.requested = requested;
        this.duplicated = duplicated;
        this.wrong = wrong;
        this.points = calculatePoints();
    }

    /*---Methods---*/
    private short calculatePoints() {
        if (duplicated || wrong) {
            return 0;
        } else {
            if (word.length() <= 4) {
                return 1;
            }
            if (word.length() == 5) {
                return 2;
            }
            if (word.length() == 6) {
                return 3;
            }
            if (word.length() == 7) {
                return 5;
            }
            return 11;
        }
    }

    /**
     * Gets the game id.
     *
     * @return the game id
     */
    public UUID getGame() {
        return game;
    }

    /**
     * Sets game id.
     *
     * @param game the game id
     */
    public void setGame(UUID game) {
        this.game = game;
    }

    /**
     * Gets player id.
     *
     * @return the player id
     */
    public String getPlayerID() {
        return playerID;
    }

    /**
     * Sets player id.
     *
     * @param playerID the player id
     */
    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    /**
     * Gets match.
     *
     * @return the match
     */
    public short getMatch() {
        return match;
    }

    /**
     * Sets match.
     *
     * @param match the match
     */
    public void setMatch(short match) {
        this.match = match;
    }

    /**
     * Gets word.
     *
     * @return the word
     */
    public String getWord() {
        return word;
    }

    /**
     * Sets word.
     *
     * @param word the word
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * Is requested boolean.
     *
     * @return requested value
     */
    public boolean isRequested() {
        return requested;
    }

    /**
     * Sets requested.
     *
     * @param requested true or false
     */
    public void setRequested(boolean requested) {
        this.requested = requested;
    }

    /**
     * Is duplicated boolean.
     *
     * @return duplicated value
     */
    public boolean isDuplicated() {
        return duplicated;
    }

    /**
     * Sets duplicated.
     *
     * @param duplicated true or false
     */
    public void setDuplicated(boolean duplicated) {
        this.duplicated = duplicated;
    }

    /**
     * Is wrong boolean.
     *
     * @return wrong value
     */
    public boolean isWrong() {
        return wrong;
    }

    /**
     * Sets wrong.
     *
     * @param wrong true or false
     */
    public void setWrong(boolean wrong) {
        this.wrong = wrong;
    }

    /**
     * Gets points.
     *
     * @return the points
     */
    public short getPoints() {
        return points;
    }

    /**
     * Sets points.
     *
     * @param points the points
     */
    public void setPoints(short points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "GameEntry{" +
                "game=" + game +
                ", playerID='" + playerID + '\'' +
                ", match=" + match +
                ", word='" + word + '\'' +
                ", requested=" + requested +
                ", duplicated=" + duplicated +
                ", wrong=" + wrong +
                ", points=" + points +
                '}';
    }
}
