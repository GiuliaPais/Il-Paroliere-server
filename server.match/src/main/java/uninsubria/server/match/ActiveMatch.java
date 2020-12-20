package uninsubria.server.match;

import uninsubria.server.scoreCounter.PlayerScore;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.Player;
import uninsubria.utils.languages.Language;

import java.util.ArrayList;
import java.util.HashMap;

public class ActiveMatch extends AbstractMatch implements ActiveMatchInterface {

    private String[] wordsFounded;
    private String[] duplicatedWords;
    private PlayerScore[] scores;
    private Language language;
    private HashMap<PlayerWrapper, Integer> playersScore;

    public ActiveMatch() {

    }

    public ActiveMatch(int numMatch, Grid grid, ArrayList<PlayerWrapper> participants, Language language) {
        super.matchNo = numMatch;
        super.participants = participants;
        super.grid = grid;

        this.language = language;

        playersScore = new HashMap<>();
    }

    /**
     * Lancia i dadi e li dispone sulla griglia.
     */
    @Override
    public void throwDices() {
        super.grid.resetDices();
        super.grid.throwDices();
    }

    /**
     * Calcola il punteggio di ogni giocatore.
     */
    @Override
    public void calculateScore() {
//        roomManager.waitWords();
//        scores = roomManager.getPlayersScore();
//
//        DuplicateWords dp = new DuplicateWords(roomManager.getLanguage());

//        for(PlayerScore ps : scores)
//            dp.addWords(ps.getWords());
//
//        wordsFounded = dp.getWordsAsString();
//
//        for(PlayerScore ps : scores)
//            ps.setDuplicateWords(dp.getDuplicatedWords());
//
//        duplicatedWords = dp.getDuplicateAsString();
    }

    /**
     * Restituisce un array di PlayerScore contenenti tutti i punteggi dei giocatori.
     * @return Un array di PlayerScores contenenti i punteggi.
     */
    public PlayerScore[] getScores() {
        return scores;
    }

    /**
     * Conclude il turno.
     */
    @Override
    public void conclude() {
//        roomManager.sendScores(scores);

        for(int i = 0; i < super.participants.size(); i++) {
            PlayerScore score = scores[i];
            playersScore.put(score.getPlayer(), score.getTotalScore());
        }
    }

    /**
     * Restituisce l'HashMap contenente il player ed il suo attuale punteggio.
     * @return l'HashMap contenente il player ed il suo attuale punteggio.
     */
    public HashMap<PlayerWrapper, Integer> getPlayersScore() {
        return playersScore;
    }

}
