package uninsubria.server.db.businesslayer;

import uninsubria.utils.languages.Language;

import java.util.Arrays;
import java.util.UUID;

/**
 * Represents a mapping of a tuple in the GameInfo table of the database.
 *
 * @author Giulia Pais
 * @author Alessandro Lerro
 * @version 0.9.0
 */
public class GameInfo {
    /*---Fields---*/
    private UUID gameId;
    private String[] allMatchesGrid;
    private Byte numPlayers;
    private String ruleset; //temporaneo, bisogna fare classi
    private Language language;

    /*---Constructors---*/
    /**
     * Instantiates a new Game info.
     */
    public GameInfo() {}

    /**
     * Instantiates a new Game info.
     *
     * @param grid       the grid for all matches
     * @param numPlayers the num players
     * @param ruleset    the ruleset
     * @param lang       the language
     */
    public GameInfo(String[] grid, Byte numPlayers, String ruleset, String lang) {
        this.gameId = UUID.randomUUID();
        this.allMatchesGrid = grid;
        this.numPlayers = numPlayers;
        this.ruleset = ruleset;
        this.language = Language.valueOf(lang);
    }

    /*---Methods---*/

    /**
     * Gets game id.
     *
     * @return the game id
     */
    public UUID getGameId() {
        return gameId;
    }

    /**
     * Sets game id.
     *
     * @param gameId the game id
     */
    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    /**
     * Get the cumulative grid for this game.
     *
     * @return the cumulative grid
     */
    public String[] getAllMatchesGrid() {
        return allMatchesGrid;
    }

    /**
     * Sets all matches grid.
     *
     * @param allMatchesGrid the all matches grid
     */
    public void setAllMatchesGrid(String[] allMatchesGrid) {
        this.allMatchesGrid = allMatchesGrid;
    }

    /**
     * Gets num players.
     *
     * @return the num players
     */
    public Byte getNumPlayers() {
        return numPlayers;
    }

    /**
     * Sets num players.
     *
     * @param numPlayers the num players
     */
    public void setNumPlayers(Byte numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**
     * Gets ruleset.
     *
     * @return the ruleset
     */
    public String getRuleset() {
        return ruleset;
    }

    /**
     * Sets ruleset.
     *
     * @param ruleset the ruleset
     */
    public void setRuleset(String ruleset) {
        this.ruleset = ruleset;
    }

    /**
     * Gets language.
     *
     * @return the language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets language.
     *
     * @param language the language
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "gameId=" + gameId +
                ", allMatchesGrid=" + Arrays.toString(allMatchesGrid) +
                ", numPlayers=" + numPlayers +
                ", ruleset='" + ruleset + '\'' +
                ", language=" + language +
                '}';
    }
}
