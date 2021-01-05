package uninsubria.server.wrappers;

import uninsubria.server.match.Match;
import uninsubria.utils.business.Word;
import uninsubria.utils.business.WordRequest;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

/**
 * Utility object, wraps useful information for the service that requests game stats registration.
 * Holds a list of all matches and the set of requested words (by all players).
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class GameEntriesWrapper {
    /*---Fields---*/
    private final List<Match> matchesInfo;
    private final HashSet<WordRequest> wordsRequested;

    /*---Constructors---*/
    /**
     * Instantiates a new Game entries wrapper.
     *
     * @param matchesInfo    the matches info
     * @param wordsRequested the words requested
     */
    public GameEntriesWrapper(List<Match> matchesInfo, HashSet<WordRequest> wordsRequested) {
        this.matchesInfo = matchesInfo;
        this.wordsRequested = wordsRequested;
    }

    /*---Methods---*/
    public List<Match> getMatchesInfo() {
        return matchesInfo;
    }

    public HashSet<WordRequest> getWordsRequested() {
        return wordsRequested;
    }

    public boolean wasRequested(String word, int matchNo) {
        for (WordRequest wr : wordsRequested) {
            boolean k = wr.getWord().equals(word) & wr.getMatchRequested() == matchNo;
            if (k) {
                return true;
            }
        }
        return false;
    }

    public Hashtable<String, Word[]> getMatchWords(int matchNumber) {
        for (Match m : matchesInfo) {
            if (m.getMatchNo() == matchNumber) {
                return m.getMatchWords();
            }
        }
        return null;
    }
}
