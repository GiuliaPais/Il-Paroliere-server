package uninsubria.server.match;

import uninsubria.server.roomReference.RoomManager;
import uninsubria.server.scoreCounter.DuplicateWords;
import uninsubria.server.scoreCounter.PlayerScore;
import uninsubria.utils.business.Player;

import java.util.HashMap;

public class ActiveMatch extends AbstractMatch implements ActiveMatchInterface {

    private String[] wordsFounded;
    private String[] duplicatedWords;
    private PlayerScore[] scores;
    private RoomManager roomManager;
    private HashMap<Player, Integer> playersScore;

    public ActiveMatch(int numMatch, Player[] p, Grid grid, RoomManager rm) {
        super.matchNo = numMatch;
        super.participants = p;
        roomManager = rm;
        super.grid = grid;
        playersScore = new HashMap<>();
    }

    /**
     * Lancia i dadi e li dispone sulla griglia.
     */
    @Override
    public void throwDices() {
        super.grid.resetDices();
        super.grid.throwDices();
        roomManager.sendGrid(super.grid.getDiceFaces(), super.grid.getDiceNumb());
    }

    /**
     * Calcola il punteggio di ogni giocatore.
     */
    @Override
    public void calculateScore() {
        roomManager.waitWords();
        scores = roomManager.getPlayersScore();

        DuplicateWords dp = new DuplicateWords(roomManager.getLanguage());

        for(PlayerScore ps : scores)
            dp.addWords(ps.getWords());

        wordsFounded = dp.getWordsAsString();

        for(PlayerScore ps : scores)
            ps.setDuplicateWords(dp.getDuplicatedWords());

        duplicatedWords = dp.getDuplicateAsString();
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
        roomManager.sendScores(scores);

        for(int i = 0; i < super.participants.length; i++) {
            PlayerScore score = scores[i];
            playersScore.put(score.getPlayer(), score.getTotalScore());
        }
    }

    /**
     * Restituisce l'HashMap contenente il player ed il suo attuale punteggio.
     * @return l'HashMap contenente il player ed il suo attuale punteggio.
     */
    public HashMap<Player, Integer> getPlayersScore() {
        return playersScore;
    }

}
