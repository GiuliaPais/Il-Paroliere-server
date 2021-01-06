package uninsubria.server.match;

import uninsubria.utils.business.Word;

import java.util.Hashtable;

/**
 * Simple object representing a concluded match. Saves its index (number) in the game and associated proposed words
 * for each player for later retrieval and storage in the database.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class Match {
    /*---Fields---*/
    private final int matchNo;
    private final Hashtable<String, Word[]> matchWords;

    /*---Constructors---*/
    /**
     * Instantiates a new Match.
     *
     * @param matchNo    the match no
     * @param matchWords the match words
     */
    public Match(int matchNo, Hashtable<String, Word[]> matchWords) {
        this.matchNo = matchNo;
        this.matchWords = matchWords;
    }

    /*---Methods---*/
    /**
     * Gets match number.
     *
     * @return the match no
     */
    public int getMatchNo() {
        return matchNo;
    }

    /**
     * Gets match words.
     *
     * @return the match words
     */
    public Hashtable<String, Word[]> getMatchWords() {
        return matchWords;
    }

}
