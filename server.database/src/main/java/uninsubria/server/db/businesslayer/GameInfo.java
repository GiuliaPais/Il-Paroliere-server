package uninsubria.server.db.businesslayer;

import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

import java.util.Arrays;
import java.util.UUID;

/**
 * Represents a mapping of a tuple in the GameInfo table of the database.
 *
 * @author Giulia Pais
 * @author Alessandro Lerro
 * @version 0.9.1
 */
public class GameInfo {
    /*---Fields---*/
    private UUID gameId;
    private String[] allMatchesGrid;
    private Integer numPlayers;
    private Ruleset ruleset;
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
    public GameInfo(UUID id, String[] grid, Integer numPlayers, Ruleset ruleset, Language lang) {
        this.gameId = id;
        this.allMatchesGrid = grid;
        this.numPlayers = numPlayers;
        this.ruleset = ruleset;
        this.language = lang;
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
    public Integer getNumPlayers() {
        return numPlayers;
    }

    /**
     * Sets num players.
     *
     * @param numPlayers the num players
     */
    public void setNumPlayers(Integer numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**
     * Gets ruleset.
     *
     * @return the ruleset
     */
    public Ruleset getRuleset() {
        return ruleset;
    }

    /**
     * Sets ruleset.
     *
     * @param ruleset the ruleset
     */
    public void setRuleset(Ruleset ruleset) {
        this.ruleset = ruleset;
    }

    /**
     * Sets ruleset.
     *
     * @param ruleset the ruleset
     */
    public void setRuleset(String ruleset) {
        this.ruleset = Ruleset.valueOf(ruleset);
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

    /**
     * Sets language.
     *
     * @param language the language
     */
    public void setLanguage(String language) {
        this.language = Language.valueOf(language);
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
