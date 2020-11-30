package uninsubria.server.match;

import uninsubria.server.roomReference.RoomManager;
import uninsubria.server.scoreCounter.DuplicateWords;
import uninsubria.server.scoreCounter.PlayerScore;
import uninsubria.utils.business.Player;


public class ActiveMatch extends AbstractMatch implements ActiveMatchInterface {

    private String[] wordsFounded;
    private String[] duplicatedWords;
    private PlayerScore[] scores;
    private RoomManager roomManager;

    public ActiveMatch(int numMatch, Player[] p, Grid grid, RoomManager rm) {
        super.matchNo = numMatch;
        super.participants = p;
        roomManager = rm;
        super.grid = grid;
    }

    /**
     * Lancia i dadi e li dispone sulla griglia.
     */
    @Override
    public void throwDices() {
        super.grid.throwDices();
    }

    /**
     * Calcola il punteggio di ogni giocatore.
     */
    @Override
    public void calculateScore() {
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
     * Conclude il turno e resetta i dadi.
     */
    @Override
    public void Conclude() {
        super.grid.resetDices();
    }

}
