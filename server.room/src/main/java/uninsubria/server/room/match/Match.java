package uninsubria.server.room.match;

import uninsubria.utils.business.Word;

import java.util.Hashtable;

/**
 * Simple object representing a concluded match. Saves it's position (number) in the game and associated scores
 * for later retrieval and storage.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class Match {
    /*---Fields---*/
    private int matchNo;
    private Hashtable<String, Word[]> matchWords;

    /*---Constructors---*/
    public Match(int matchNo, Hashtable<String, Word[]> matchWords) {
        this.matchNo = matchNo;
        this.matchWords = matchWords;
    }

    /*---Methods---*/

}
